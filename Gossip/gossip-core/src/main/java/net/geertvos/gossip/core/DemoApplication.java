package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.geertvos.gossip.core.network.GossipServer;

import org.apache.log4j.BasicConfigurator;

public class DemoApplication {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		Random random = new Random(System.currentTimeMillis());
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>();
		for(int i=0; i<10; i++) {
			GossipClusterMember member = new GossipClusterMember(""+i, "localhost", 8000+i, System.currentTimeMillis(),"");
			members.add(member);
		}
		for(int i=0; i<10; i++) {
			GossipClusterMember member = members.get(random.nextInt(members.size()));
			GossipCluster cluster = new GossipCluster(""+i, "localhost", 8000+i, member );

			GossipServer server1 = new GossipServer(cluster);
			server1.start();
		}
		
	}
	
}
