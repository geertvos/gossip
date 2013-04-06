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

public class GossipServer {

	private static final int GOSSIP_INTERVAL = 500;

	private final GossipCluster cluster;
	
	private volatile boolean running = true;
	private ServerBootstrap serverBootstrap;
	private ClientBootstrap clientBootstrap;
	private Channel serverChannel;
	
	public GossipServer(GossipCluster cluster) {
		this.cluster = cluster;
		
		serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new GossipPipelineFactory(cluster,false));

		clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		clientBootstrap.setPipelineFactory(new GossipPipelineFactory(cluster,true));
		
	}
	
	public void start() {
		serverChannel = serverBootstrap.bind(new InetSocketAddress(cluster.getHost(), cluster.getPort()));

		Thread runner = new Thread(new RandomGossip(),"GossipServer Thread");
		runner.start();
		
	}
	
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
					Thread.sleep(GOSSIP_INTERVAL);
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
			ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(member.getIp(),member.getPort()));
			future.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						future.getChannel().write(cluster.createGossipMessage());
					}
				}
			});
		}
		
	}
	
}
