package net.geertvos.gossip.core.network;

import java.io.IOException;

import net.geertvos.gossip.core.GossipCluster;
import net.geertvos.gossip.core.GossipMessage;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class GossipMessageHandler extends SimpleChannelHandler {

	private final GossipCluster cluster;
	private final boolean disconnect;
	
	public GossipMessageHandler(GossipCluster cluster, boolean disconnect) {
		this.cluster = cluster;
		this.disconnect = disconnect;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		GossipMessage message = (GossipMessage) e.getMessage();
		GossipMessage reply = cluster.handleGossip(message);
		ChannelFuture future = ctx.getChannel().write(reply);
		if(disconnect) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if(e.getCause() instanceof IOException) {
			//ignore
		} else {
			e.getCause().printStackTrace();
		}
	}

}
