package net.geertvos.gossip.api.cluster;

public interface ClusterMember {

	public String getId();
	
	public String getIp();
	
	public int getPort();
	
	public long getLastSeenOnline();

}
