package net.geertvos.gossip.core.network;

import static org.jboss.netty.channel.Channels.write;

import net.geertvos.gossip.core.GossipMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class GossipMessageEncoder extends SimpleChannelHandler {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
		if (!(evt instanceof MessageEvent)) {
			ctx.sendDownstream(evt);
			return;
		}

		MessageEvent e = (MessageEvent) evt;
		GossipMessage originalMessage = (GossipMessage) e.getMessage();
		String json = mapper.writeValueAsString(originalMessage)+"\n";
		write(ctx, e.getFuture(), json, e.getRemoteAddress());
	}

}
