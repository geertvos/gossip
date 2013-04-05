package net.geertvos.gossip.api.cluster;

public interface ClusterEventListener {

	public void onNewActiveMember(ClusterMember member);
	
	public void onNewInactiveMember(ClusterMember member);
	
	public void onMemberActivated(ClusterMember member);
	
	public void onMemberDeactivated(ClusterMember member);
	
	public void onClusterStabalized();
	
	public void onClusterDestabilized();
	
}
