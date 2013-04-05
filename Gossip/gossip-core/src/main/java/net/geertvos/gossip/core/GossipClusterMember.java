package net.geertvos.gossip.core;

import net.geertvos.gossip.api.cluster.ClusterMember;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GossipClusterMember implements ClusterMember {

	private String id;
	private String ip;
	private int port;
	private long lastSeenOnline;
	private String hash;

	public GossipClusterMember() {
	}
	
	public GossipClusterMember(String id, String ip, int port, long lastSeenOnline, String hash) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.lastSeenOnline = lastSeenOnline;
		this.hash = hash;
	}


	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public long getLastSeenOnline() {
		return lastSeenOnline;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getHash() {
		return hash;
	}

	public void setLastSeenOnline(long lastSeenOnline) {
		this.lastSeenOnline = lastSeenOnline;
	}

}
