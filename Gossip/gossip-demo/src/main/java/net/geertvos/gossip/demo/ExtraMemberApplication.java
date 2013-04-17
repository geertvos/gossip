package net.geertvos.gossip.demo;

import net.geertvos.gossip.core.GossipCluster;
import net.geertvos.gossip.core.GossipClusterMember;
import net.geertvos.gossip.core.network.GossipServer;

import org.apache.log4j.BasicConfigurator;

public class ExtraMemberApplication {

	private static final String CLUSTER = "demoCluster";
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		GossipClusterMember member = new GossipClusterMember("Computer", "192.168.0.104", 9000, 0,"");
		GossipCluster cluster = new GossipCluster(CLUSTER, "Laptop", "192.168.0.103", 9000, member );
		GossipServer server1 = new GossipServer(cluster);
		server1.start();
	}
	
}
