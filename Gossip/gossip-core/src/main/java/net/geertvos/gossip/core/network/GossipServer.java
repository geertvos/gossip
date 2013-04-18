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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.core.GossipCluster;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author Geert Vos
 */
public class GossipServer {

	private int gossipInterval = 500;

	private final GossipCluster cluster;
	
	private volatile boolean running = true;
	private ServerBootstrap serverBootstrap;
	private ClientBootstrap clientBootstrap;
	private Channel serverChannel;
	
	/**
	 * Create a server for this cluster.
	 * @param cluster
	 */
	public GossipServer(GossipCluster cluster) {
		this.cluster = cluster;
		
		serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new GossipPipelineFactory(cluster,false));

		clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		clientBootstrap.setPipelineFactory(new GossipPipelineFactory(cluster,true));
		
	}
	
	/**
	 * Set the gossip interval. Default is 500 ms. Set this value to low and the cluster will use a lot of network bandwidth and CPU.
	 * Set it too high, and the it will take a while for the cluster to 'discover' nodes and failures.
	 * 
	 * @param intervalInMs
	 */
	public void setGossipInterval(int intervalInMs) {
		this.gossipInterval = intervalInMs;
	}
	
	/**
	 * Starts the server for this cluster.
	 */
	public void start() {
		serverChannel = serverBootstrap.bind(new InetSocketAddress(cluster.getHost(), cluster.getPort()));
		Thread runner = new Thread(new RandomGossip(),"GossipServer Thread");
		runner.start();
	}
	
	/**
	 * Stop the server for this cluster.
	 */
	public void shutdown() {
		running = false;
		serverChannel.close();
	}
	
	class RandomGossip implements Runnable {

		private final Random random = new Random(System.currentTimeMillis());
		
		@Override
		public void run() {
			while(running) {
				randomGossip();
				try {
					Thread.sleep(gossipInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void randomGossip() {
			List<ClusterMember> members = cluster.getActiveMembers();
			if(members.isEmpty()) {
				members.addAll(cluster.getPassiveMembers());
			}
			if(!members.isEmpty()) {
				int index = random.nextInt(members.size());
				ClusterMember member = members.get(index);
				gossipWith(member);
			}
		}

		private void gossipWith(final ClusterMember member) {
			ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(member.getHost(),member.getPort()));
			future.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						ChannelFuture f = future.getChannel().write(cluster.createGossipMessage(member));
						f.addListener(ChannelFutureListener.CLOSE);
					}
				}
			});
		}
		
	}
	
}
