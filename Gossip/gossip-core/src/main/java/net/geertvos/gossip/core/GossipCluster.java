package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

import net.geertvos.gossip.api.cluster.Cluster;
import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.api.cluster.ClusterState;

public class GossipCluster implements Cluster {

	private static final int DEADNODE_DELAY = 2000;
	private ConcurrentSkipListMap<String,GossipClusterMember> activeMembers = new ConcurrentSkipListMap<String, GossipClusterMember>();
	private ConcurrentSkipListMap<String,GossipClusterMember> passiveMembers = new ConcurrentSkipListMap<String, GossipClusterMember>();
	private AtomicReference<ClusterState> clusterState = new AtomicReference<ClusterState>(ClusterState.UNSTABLE);
	private volatile ClusterEventListener listener = null;
	
	private final String id;
	private final String host;
	private final int port;
	
	public GossipCluster(String id, String host, int port, GossipClusterMember ... members) {
		this.id = id;
		this.host = host;
		this.port = port;
		for(GossipClusterMember member : members) {
			passiveMembers.put(member.getId(), member);
		}
	}
	
	@Override
	public List<ClusterMember> getActiveMembers() {
		return new ArrayList<ClusterMember>(activeMembers.descendingMap().values());
	}

	@Override
	public List<ClusterMember> getPassiveMembers() {
		return new ArrayList<ClusterMember>(passiveMembers.descendingMap().values());
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
		this.listener = listener;
	}
	
	public GossipMessage handleGossip(GossipMessage message) {
		for(GossipClusterMember member : message.getMemberInfo()) {
			if(member.getId().equals(id)) {
				//Skip self
				continue;
			}
			boolean active = System.currentTimeMillis() - member.getLastSeenOnline() < DEADNODE_DELAY;
			if(activeMembers.containsKey(member.getId())) {
				GossipClusterMember existing = activeMembers.get(member.getId());
				if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
					handleNewerMemberInfo(member, active);
				} else {
					handleOlderMemberInfo(existing);
				}
			} else if(passiveMembers.containsKey(member.getId())) {
				GossipClusterMember existing = passiveMembers.get(member.getId());
				if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
					if(active) {
						passiveMembers.remove(member.getId());
						activeMembers.put(member.getId(), member);
						if(listener!=null) {
							listener.onMemberActivated(member);
						}
					} else {
						passiveMembers.put(member.getId(), member);
					}
				}
			} else {
				if(active) {
					handleNewActiveMember(member);
				} else {
					handleNewInactiveMember(member);
				}
			}
		}
		return generateMessage();
	}

	private void handleNewInactiveMember(GossipClusterMember member) {
		passiveMembers.put(member.getId(), member);
		if(listener!=null) {
			listener.onNewInactiveMember(member);
		}
	}

	private void handleNewActiveMember(GossipClusterMember member) {
		activeMembers.put(member.getId(), member);
		if(listener!=null) {
			listener.onNewActiveMember(member);
		}
	}

	private void handleOlderMemberInfo(GossipClusterMember existing) {
		boolean existingActive = System.currentTimeMillis() - existing.getLastSeenOnline() < DEADNODE_DELAY;
		if(!existingActive) {
			activeMembers.remove(existing.getId());
			passiveMembers.put(existing.getId(), existing);
			if(listener!=null) {
				listener.onMemberDeactivated(existing);
			}
		}
	}

	private void handleNewerMemberInfo(GossipClusterMember member, boolean active) {
		if(active) {
			activeMembers.put(member.getId(), member);
		} else {
			activeMembers.remove(member.getId());
			passiveMembers.put(member.getId(), member);
			if(listener!=null) {
				listener.onMemberDeactivated(member);
			}
		}
	}

	public GossipMessage generateMessage() {
		GossipMessage reply = new GossipMessage();
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>(activeMembers.descendingMap().values());
		members.add(new GossipClusterMember(id, host, port, System.currentTimeMillis()));
		reply.setMemberInfo(members);
		return reply;
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
