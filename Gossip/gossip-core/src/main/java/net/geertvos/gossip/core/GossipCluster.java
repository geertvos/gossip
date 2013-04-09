package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.geertvos.gossip.api.cluster.Cluster;
import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterHashProvider;
import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.api.cluster.ClusterState;
import net.geertvos.gossip.core.threading.GossipClusterTask;
import net.geertvos.gossip.core.threading.NamedThreadFactory;

import org.apache.log4j.Logger;

public class GossipCluster implements Cluster {

	private final Logger logger = Logger.getLogger(GossipCluster.class);
	
	private static final int DEADNODE_DELAY = 5000;
	
	private final LinkedHashMap<String,GossipClusterMember> activeMembers = new LinkedHashMap<String, GossipClusterMember>();
	private final LinkedHashMap<String,GossipClusterMember> passiveMembers = new LinkedHashMap<String, GossipClusterMember>();
	private final List<ClusterEventListener> listeners = new ArrayList<ClusterEventListener>(10);

	private final ClusterHashProvider<GossipClusterMember> hashProvider = new Md5HashProvider();
	
	private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
	private ExecutorService executorService;
	
	private ClusterState clusterState = ClusterState.UNSTABLE;
	private String clusterStateHash = "";
	private final String id;
	private final String host;
	private final int port;
	
	public GossipCluster(String id, String host, int port, GossipClusterMember ... members) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("Gossip Cluster "+id));
		for(GossipClusterMember member : members) {
			passiveMembers.put(member.getId(), member);
		}
		scheduledExecutorService.scheduleWithFixedDelay(new CheckStabilityPeriodicaly(), 5000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public List<ClusterMember> getActiveMembers() {
		GetActivePartitipantsTask task = new GetActivePartitipantsTask();
		executorService.execute(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getPassiveMembers() {
		GetInactivePartitipantsTask task = new GetInactivePartitipantsTask();
		executorService.execute(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getMembers() {
		return null;
	}

	@Override
	public ClusterState getState() {
		return clusterState;
	}

	@Override
	public void registerClusterEventListener(ClusterEventListener listener) {
		listeners.add(listener);
	}
	
	public GossipMessage handleGossip(GossipMessage message) {
		HandleGossipMessage task = new HandleGossipMessage(message);
		executorService.execute(task);
		return task.call();
	}

	public GossipMessage createGossipMessage() {
		GenerateMessageTask task = new GenerateMessageTask();
		executorService.execute(task);
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
		GossipClusterMember me = new GossipClusterMember(id, host, port, System.currentTimeMillis(), clusterStateHash);
		members.add(me);
		clusterStateHash = hashProvider.hashCluster(members);
		me.setHash(clusterStateHash);
		reply.setMemberInfo(members);
		return reply;
	}
	
	private void checkStability() {
		CheckStabilityTask task = new CheckStabilityTask();
		executorService.execute(task);
	}
	
	private void checkDeadMembers() {
		CheckDeadMembersTask task = new CheckDeadMembersTask();
		executorService.execute(task);
	}
	
	public void notifyNewInactive(final GossipClusterMember member) {
		logger.debug("New inactive member "+member.getId());
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewInactiveMember(member);
				}
			}
		};
		executorService.execute(task);
	}
	
	public void notifyNewActive(final GossipClusterMember member) {
		logger.debug("New active member "+member.getId());
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewInactiveMember(member);
				}
			}
		};
		executorService.execute(task);
	}
	
	public void notifyMemberDeactivated(final GossipClusterMember member) {
		logger.debug("Deactivated member "+member.getId());
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberDeactivated(member);
				}
			}
		};
		executorService.execute(task);
	}
	
	public void notifyMemberActivated(final GossipClusterMember member) {
		logger.debug("Activated member "+member.getId());
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberActivated(member);
				}
			}
		};
		executorService.execute(task);
	}
	
	public void notifyStable(final boolean stable) {
		if(stable) {
			logger.debug("Cluster view stable");
		} else {
			logger.debug("Cluster view unstable");
		}
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
		executorService.execute(task);
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
	
	class CheckStabilityPeriodicaly implements Runnable {

		@Override
		public void run() {
			checkDeadMembers();
		}
		
	}
	
	class CheckStabilityTask implements Runnable {

		@Override
		public void run() {
			boolean stable = true;
			for(GossipClusterMember member : activeMembers.values()) {
				if(!member.getHash().equals(clusterStateHash)) {
					stable = false;
					if(logger.isDebugEnabled()) {
						logger.debug("Cluster not stable, "+member.getId()+" has a different view.");
					}
				}
			}
			if(clusterState.equals(ClusterState.UNSTABLE) && stable) {
				clusterState = ClusterState.STABLE;
				notifyStable(true);
			}
			else if(clusterState.equals(ClusterState.STABLE) && !stable) {
				clusterState = ClusterState.UNSTABLE;
				notifyStable(false);
			}
		}
		
	}
	
	class CheckDeadMembersTask extends CheckStabilityTask {
		
		@Override
		public void run() {
			boolean stable = true;
			for(GossipClusterMember member : activeMembers.values()) {
				boolean active = System.currentTimeMillis() - member.getLastSeenOnline() < DEADNODE_DELAY;
				if(!active) {
					activeMembers.remove(member.getId());
					passiveMembers.put(member.getId(), member);
					notifyMemberDeactivated(member);
					stable = false;
				}
			}
			if(clusterState.equals(ClusterState.STABLE) && !stable) {
				clusterState = ClusterState.UNSTABLE;
				notifyStable(false);
			}
			super.run();
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


		
	}
	
}
