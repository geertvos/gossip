package net.geertvos.gossip.core.network;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.geertvos.gossip.core.GossipClusterMember;
import net.geertvos.gossip.core.GossipMessage;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.testng.annotations.Test;

public class GossipMessageEncoderTest {

	@Test
	public void testEncoder() throws Exception {
		GossipMessageEncoder encoder = new GossipMessageEncoder();
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
		Channel channel = mock(Channel.class);
		
		when(ctx.getChannel()).thenReturn(channel);
		
		MessageEvent evt = mock(MessageEvent.class);
		when(evt.getFuture()).thenReturn(mock(ChannelFuture.class));
		
		GossipMessage gossipMessage = new GossipMessage();
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>();
		GossipClusterMember m1 = new GossipClusterMember();
		m1.setId("Id");
		
		members.add(m1);
		gossipMessage.setMemberInfo(members);
		
		when(evt.getMessage()).thenReturn(gossipMessage);
		encoder.handleDownstream(ctx, evt);
	}
	
}
