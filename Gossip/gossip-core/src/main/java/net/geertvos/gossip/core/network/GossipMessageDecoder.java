package net.geertvos.gossip.core.network;

import net.geertvos.gossip.core.GossipMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class GossipMessageDecoder extends OneToOneDecoder {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		String message = (String)msg;
		GossipMessage gossipMessage = mapper.readValue(message, GossipMessage.class);
		return gossipMessage;
	}

	
	
}
