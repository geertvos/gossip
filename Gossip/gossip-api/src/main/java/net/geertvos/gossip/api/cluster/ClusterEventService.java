package net.geertvos.gossip.api.cluster;

import net.geertvos.gossip.api.cluster.ClusterEventListener;

public interface ClusterEventService {

	void registerListener(ClusterEventListener listener);
	
	void unregisterListener(ClusterEventListener listener);
	
	public void notifyNewActiveMember(ClusterMember member);
	
	public void notifyNewInactiveMember(ClusterMember member);
	
	public void notifyMemberActivated(ClusterMember member);
	
	public void notifyMemberDeactivated(ClusterMember member);
	
	public void notifyClusterStabilized();
	
	public void notifyClusterDestabilized();
	
}
