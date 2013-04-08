package net.geertvos.gossip.demo;

import net.geertvos.gossip.core.GossipCluster;
import net.geertvos.gossip.core.GossipClusterMember;
import net.geertvos.gossip.core.network.GossipServer;

import org.apache.log4j.BasicConfigurator;

public class ExtraMemberApplication {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		GossipClusterMember member = new GossipClusterMember("Member-1", "localhost", 8001, System.currentTimeMillis(),"");
		GossipCluster cluster = new GossipCluster("X", "localhost", 9000, member );
		GossipServer server1 = new GossipServer(cluster);
		server1.start();
	}
	
}
