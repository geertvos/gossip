package net.geertvos.gossip.api.cluster;

import java.util.List;

public interface Cluster {

	public List<ClusterMember> getActiveMembers();
	
	public List<ClusterMember> getPassiveMembers();
	
	public List<ClusterMember> getMembers();
	
	public ClusterState getState();
	
	public void registerClusterEventListener(ClusterEventListener listener);
	
}
