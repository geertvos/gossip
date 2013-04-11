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

import net.geertvos.gossip.core.GossipCluster;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * @author Geert Vos
 */
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
