package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import net.geertvos.gossip.api.cluster.Cluster;
import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterHashProvider;
import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.api.cluster.ClusterState;
import net.geertvos.gossip.core.threading.GossipClusterTask;

public class GossipCluster implements Cluster {

	private static final int DEADNODE_DELAY = 2000;
	
	private LinkedHashMap<String,GossipClusterMember> activeMembers = new LinkedHashMap<String, GossipClusterMember>();
	private LinkedHashMap<String,GossipClusterMember> passiveMembers = new LinkedHashMap<String, GossipClusterMember>();
	private AtomicReference<ClusterState> clusterState = new AtomicReference<ClusterState>(ClusterState.UNSTABLE);
	private List<ClusterEventListener> listeners = new ArrayList<ClusterEventListener>(10);
	
	private ClusterHashProvider hashProvider = new Md5HashProvider();
	private String clusterStateHash = "";
	
	private volatile boolean running = true;
	private final LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

	private final String id;
	private final String host;
	private final int port;
	
	public GossipCluster(String id, String host, int port, GossipClusterMember ... members) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.hashProvider = new Md5HashProvider();
		for(GossipClusterMember member : members) {
			passiveMembers.put(member.getId(), member);
		}
		
		Thread worker = new Thread(new Worker());
		worker.start();
	}
	
	@Override
	public List<ClusterMember> getActiveMembers() {
		GetActivePartitipantsTask task = new GetActivePartitipantsTask();
		tasks.add(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getPassiveMembers() {
		GetInactivePartitipantsTask task = new GetInactivePartitipantsTask();
		tasks.add(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getMembers() {
		return null;
	}

	@Override
	public ClusterState getState() {
		return clusterState.get();
	}

	@Override
	public void registerClusterEventListener(ClusterEventListener listener) {
		listeners.add(listener);
	}
	
	public GossipMessage handleGossip(GossipMessage message) {
		HandleGossipMessage task = new HandleGossipMessage(message);
		tasks.add(task);
		return task.call();
	}

	public GossipMessage createGossipMessage() {
		GenerateMessageTask task = new GenerateMessageTask();
		tasks.add(task);
		return task.call();
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	private GossipMessage generateMessage() {
		GossipMessage reply = new GossipMessage();
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>(activeMembers.values());
		members.add(new GossipClusterMember(id, host, port, System.currentTimeMillis(), clusterStateHash));
		clusterStateHash = hashProvider.hashCluster((Collection)members);
		reply.setMemberInfo(members);
		return reply;
	}
	

	class GetActivePartitipantsTask extends GossipClusterTask<List<ClusterMember>> {

		@Override
		public void run() {
			setResult(new ArrayList<ClusterMember>(activeMembers.values()));
		}
		
	}

	class GetInactivePartitipantsTask extends GossipClusterTask<List<ClusterMember>> {

		@Override
		public void run() {
			setResult(new ArrayList<ClusterMember>(passiveMembers.values()));
		}
		
	}

	
	class GenerateMessageTask extends GossipClusterTask<GossipMessage> {

		@Override
		public void run() {
			setResult(generateMessage());
		}
		
	}
	
	class HandleGossipMessage extends GossipClusterTask<GossipMessage> {

		private final GossipMessage message;

		public HandleGossipMessage(GossipMessage message) {
			this.message = message;
		}
		

		@Override
		public void run() {
			for(GossipClusterMember member : message.getMemberInfo()) {
				if(member.getId().equals(id)) {
					//Skip self
					continue;
				}
				boolean active = System.currentTimeMillis() - member.getLastSeenOnline() < DEADNODE_DELAY;
				if(activeMembers.containsKey(member.getId())) {
					GossipClusterMember existing = activeMembers.get(member.getId());
					if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
						if(active) {
							activeMembers.put(member.getId(), member);
						} else {
							activeMembers.remove(member.getId());
							passiveMembers.put(member.getId(), member);
							notifyMemberDeactivated(member);
						}
					} else {
						boolean existingActive = System.currentTimeMillis() - existing.getLastSeenOnline() < DEADNODE_DELAY;
						if(!existingActive) {
							activeMembers.remove(existing.getId());
							passiveMembers.put(existing.getId(), existing);
							notifyMemberDeactivated(existing);
						}
					}
				} else if(passiveMembers.containsKey(member.getId())) {
					GossipClusterMember existing = passiveMembers.get(member.getId());
					if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
						if(active) {
							passiveMembers.remove(member.getId());
							activeMembers.put(member.getId(), member);
							notifyMemberActivated(member);
						} else {
							passiveMembers.put(member.getId(), member);
						}
					}
				} else {
					if(active) {
						activeMembers.put(member.getId(), member);
						notifyNewActive(member);
					} else {
						passiveMembers.put(member.getId(), member);
						notifyNewInactive(member);
					}
				}
			}
			setResult(generateMessage());
			checkStability();
		}

		private void checkStability() {
			boolean stable = true;
			for(GossipClusterMember member : activeMembers.values()) {
				if(!member.getHash().equals(clusterStateHash)) {
					stable = false;
				}
			}
			if(clusterState.get().equals(ClusterState.UNSTABLE) && stable) {
				clusterState.set(ClusterState.STABLE);
				notifyStable(true);
			}
			else if(clusterState.get().equals(ClusterState.STABLE) && !stable) {
				clusterState.set(ClusterState.UNSTABLE);
				notifyStable(false);
			}
		}

		public void notifyStable(final boolean stable) {
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					if(stable) {
						for(ClusterEventListener listener : listeners) {
							listener.onClusterStabalized();
						}
					} else {
						for(ClusterEventListener listener : listeners) {
							listener.onClusterDestabilized();
						}
					}
				}
			};
			tasks.add(task);
		}

		public void notifyNewInactive(final GossipClusterMember member) {
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					for(ClusterEventListener listener : listeners) {
						listener.onNewInactiveMember(member);
					}
				}
			};
			tasks.add(task);
		}
		
		public void notifyNewActive(final GossipClusterMember member) {
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					for(ClusterEventListener listener : listeners) {
						listener.onNewInactiveMember(member);
					}
				}
			};
			tasks.add(task);
		}
		
		public void notifyMemberDeactivated(final GossipClusterMember member) {
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					for(ClusterEventListener listener : listeners) {
						listener.onMemberDeactivated(member);
					}
				}
			};
			tasks.add(task);
		}
		
		public void notifyMemberActivated(final GossipClusterMember member) {
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					for(ClusterEventListener listener : listeners) {
						listener.onMemberActivated(member);
					}
				}
			};
			tasks.add(task);
		}
		
	}
	
	class Worker implements Runnable {

		@Override
		public void run() {
			while(running) {
				try {
					Runnable r = tasks.take();
					try{ 
						r.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
				}
			}
		}
		
	}


}
