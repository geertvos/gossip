package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import net.geertvos.gossip.api.cluster.ClusterEventListener;
import static org.mockito.Mockito.*;
public class GossipClusterTest {

	@Test
	public void testHandleGossipMessage() {
		
		GossipCluster cluster = new GossipCluster("me","localhost",8888);
		
		GossipMessage gossipMessage = new GossipMessage();
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>();
		GossipClusterMember m1 = new GossipClusterMember();
		m1.setId("offline");
		m1.setLastSeenOnline(System.currentTimeMillis()-5000);
		members.add(m1);
		
		GossipClusterMember m2 = new GossipClusterMember();
		m2.setId("online");
		m2.setLastSeenOnline(System.currentTimeMillis());
		members.add(m2);

		gossipMessage.setMemberInfo(members);

		ClusterEventListener listener = mock(ClusterEventListener.class);
		cluster.registerClusterEventListener(listener);
		
		cluster.handleGossip(gossipMessage);
		
		verify(listener).onNewInactiveMember(m1);
		verify(listener).onNewActiveMember(m2);
		
		GossipMessage gossipMessage2 = new GossipMessage();
		List<GossipClusterMember> members2 = new ArrayList<GossipClusterMember>();
		GossipClusterMember m3 = new GossipClusterMember("offline","ip",8888,System.currentTimeMillis()-10,"");
		members2.add(m3);
		gossipMessage2.setMemberInfo(members2);

		cluster.handleGossip(gossipMessage2);
		verify(listener).onMemberActivated(m3);
		
	}
	
}
