package net.geertvos.gossip.core;

import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.core.network.GossipServer;

public class DemoApplication {

	public static void main(String[] args) {
		
		GossipClusterMember member1 = new GossipClusterMember("1", "localhost", 8002, System.currentTimeMillis());
		GossipClusterMember member2 = new GossipClusterMember("2", "localhost", 8002, System.currentTimeMillis());
		GossipCluster cluster1 = new GossipCluster("1", "localhost", 8001, member2 );
		GossipCluster cluster2 = new GossipCluster("2", "localhost", 8002, member1 );

		cluster1.registerClusterEventListener(new ClusterEventListener() {
			
			@Override
			public void onNewInactiveMember(ClusterMember member) {
				System.out.println("Mew inactive member: "+member.getId());
			}
			
			@Override
			public void onNewActiveMember(ClusterMember member) {
				System.out.println("Mew active member: "+member.getId());
			}
			
			@Override
			public void onMemberDeactivated(ClusterMember member) {
				System.out.println("Deactivated member: "+member.getId());
			}
			
			@Override
			public void onMemberActivated(ClusterMember member) {
				System.out.println("Activated member: "+member.getId());
			}
			
			@Override
			public void onClusterStabalized() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClusterDestabalized() {
				// TODO Auto-generated method stub
				
			}
		});
		
		GossipServer server1 = new GossipServer(cluster1);
		GossipServer server2 = new GossipServer(cluster2);
		
	}
	
}
