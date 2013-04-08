package net.geertvos.gossip.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.geertvos.gossip.core.GossipCluster;
import net.geertvos.gossip.core.GossipClusterMember;
import net.geertvos.gossip.core.network.GossipServer;

import org.apache.log4j.BasicConfigurator;

public class DemoApplication {

	private static final String HOST = "localhost";
	private static final int HOST_COUNT = 10;

	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		Random random = new Random(System.currentTimeMillis());
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>();
		for(int i=0; i < HOST_COUNT; i++) {
			GossipClusterMember member = new GossipClusterMember(generateId(i), HOST, 8000+i, System.currentTimeMillis());
			members.add(member);
		}
		for(int i=0; i < HOST_COUNT; i++) {
			GossipClusterMember member = members.get(random.nextInt(members.size()));
			GossipCluster cluster = new GossipCluster(generateId(i), HOST, 8000+i, member );

			GossipServer server1 = new GossipServer(cluster);
			server1.start();
		}
	}

	private static String generateId(int id) {
		return "Member-"+id;
	}
	
}
