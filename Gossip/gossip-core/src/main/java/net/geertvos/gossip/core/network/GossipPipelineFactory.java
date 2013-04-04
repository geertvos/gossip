package net.geertvos.gossip.core.network;

import net.geertvos.gossip.core.GossipCluster;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class GossipPipelineFactory implements ChannelPipelineFactory {

	private final GossipCluster cluster;
	private final boolean disconnect;
	
	public GossipPipelineFactory(GossipCluster cluster, boolean disconnect) {
		this.cluster = cluster;
		this.disconnect = disconnect;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		pipeline.addLast("messageDecoder", new GossipMessageDecoder());
		pipeline.addLast("messageEncoder", new GossipMessageEncoder());
		pipeline.addLast("handler", new GossipMessageHandler(cluster, disconnect));
		return pipeline;
	}

}
