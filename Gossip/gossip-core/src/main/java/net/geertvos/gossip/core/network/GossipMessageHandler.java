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

import java.io.IOException;

import net.geertvos.gossip.core.GossipCluster;
import net.geertvos.gossip.core.GossipMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * @author Geert Vos
 */
public class GossipMessageHandler extends SimpleChannelHandler {

	private static final Logger LOG = Logger.getLogger(GossipMessageHandler.class);
	private final GossipCluster cluster;
	private final boolean disconnect;
	
	public GossipMessageHandler(GossipCluster cluster, boolean disconnect) {
		this.cluster = cluster;
		this.disconnect = disconnect;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		GossipMessage message = (GossipMessage) e.getMessage();
		cluster.handleGossip(message);
		//TODO: Fix reply for way better clustering!
//		if(reply != null) {
//			LOG.info("Wrote reply to "+message.getFrom());
//
//			ChannelFuture future = ctx.getChannel().write(reply);
//			if(disconnect) {
//				future.addListener(ChannelFutureListener.CLOSE);
//			}
//		} else {
			ctx.getChannel().close();
//		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if(e.getCause() instanceof IOException) {
			//ignore for now, we need to handle failed connections later.
		} else {
			e.getCause().printStackTrace();
		}
	}

}
