package net.geertvos.gossip.core.network;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class GossipMessageEncoder extends OneToOneEncoder {

	private final static ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		return mapper.writeValueAsString(msg)+"\n";
	}

}
