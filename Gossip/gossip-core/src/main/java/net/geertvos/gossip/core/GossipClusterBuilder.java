package net.geertvos.gossip.core;

import java.util.HashMap;
import java.util.Map;

import net.geertvos.gossip.core.network.GossipServer;

public class GossipClusterBuilder {

	private String clusterName = "cluster";
	private Map<String,String> metadata = new HashMap<String, String>();
	private String hostname = "localhost";
	private int port = 8000;
	private GossipClusterMember seedMember;
	private String myMemberId = "Master";
	
	public GossipClusterBuilder() {
	}

	public GossipClusterBuilder clusterName(String name) {
		this.clusterName = name;
		return this;
	}
	
	public GossipClusterBuilder memberName(String name) {
		this.myMemberId = name;
		return this;
	}
	
	public GossipClusterBuilder withMetadata(Map<String,String> metadata) {
		this.metadata = metadata;
		return this;
	}
	
	public GossipClusterBuilder onHost(String hostname) {
		this.hostname = hostname;
		return this;
	}
	
	public GossipClusterBuilder onPort(int port) {
		this.port = port;
		return this;
	}
	
	public GossipClusterBuilder withSeedMember(String id, String host, int port) {
		this.seedMember = new GossipClusterMember(id, host, port, System.currentTimeMillis());
		return this;
	}

	public GossipClusterBuilder withSeedMember(String id, int port) {
		this.seedMember = new GossipClusterMember(id, hostname, port, System.currentTimeMillis());
		return this;
	}
	
	public GossipCluster build() {
		if(seedMember == null) {
			throw new IllegalStateException("At least a seedMember must be provided.");
		}
		GossipCluster cluster = new GossipCluster(clusterName, myMemberId, hostname, port, metadata , seedMember);
		GossipServer server1 = new GossipServer(cluster);
		server1.start();
		return cluster;
	}

	public GossipClusterBuilder withMetadata(String key, String value) {
		if(metadata==null) {
			metadata = new HashMap<String, String>();
		}
		metadata.put(key, value);
		return this;
	}
	
}
