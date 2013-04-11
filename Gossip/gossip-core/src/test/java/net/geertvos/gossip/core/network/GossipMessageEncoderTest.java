/**
    This file is part of the Java Gossip Cluster Framework.

    The Java Gossip Framework is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Java Gossip Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * @author Geert Vos
 */
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
