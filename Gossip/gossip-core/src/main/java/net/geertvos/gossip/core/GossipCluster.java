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
package net.geertvos.gossip.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.geertvos.gossip.api.cluster.Cluster;
import net.geertvos.gossip.api.cluster.ClusterEventService;
import net.geertvos.gossip.api.cluster.ClusterHashProvider;
import net.geertvos.gossip.api.cluster.ClusterMember;
import net.geertvos.gossip.api.cluster.ClusterState;
import net.geertvos.gossip.core.threading.GossipClusterTask;
import net.geertvos.gossip.core.threading.NamedThreadFactory;

import org.apache.log4j.Logger;

/**
 * @author Geert Vos
 */
public class GossipCluster implements Cluster {

	private final Logger logger = Logger.getLogger(GossipCluster.class);
	
	private static final int DEADNODE_DELAY = 5000;
	
	private final LinkedHashMap<String,GossipClusterMember> activeMembers = new LinkedHashMap<String, GossipClusterMember>();
	private final LinkedHashMap<String,GossipClusterMember> passiveMembers = new LinkedHashMap<String, GossipClusterMember>();
	private final ClusterHashProvider<GossipClusterMember> hashProvider = new Md5HashProvider();
	private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
	private final String clusterId;
	
	private ClusterEventService eventService = new GossipClusterEventService();
	private ExecutorService executorService;
	private ClusterState clusterState = ClusterState.UNSTABLE;
	private String clusterStateHash = "";
	private final String memberId;
	private final String host;
	private final int port;
	
	public GossipCluster(String clusterId, String memberId, String host, int port, GossipClusterMember ... members) {
		this.clusterId = clusterId;
		this.memberId = memberId;
		this.host = host;
		this.port = port;
		this.executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("Gossip Cluster "+memberId));
		for(GossipClusterMember member : members) {
			if(member.getId().equals(memberId)) {
				throw new IllegalStateException("Cannot add self to cluster.");
			}
			passiveMembers.put(member.getId(), member);
		}
		scheduledExecutorService.scheduleWithFixedDelay(new CheckStabilityPeriodicaly(), 5000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public List<ClusterMember> getActiveMembers() {
		GetActivePartitipantsTask task = new GetActivePartitipantsTask();
		executorService.execute(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getPassiveMembers() {
		GetInactivePartitipantsTask task = new GetInactivePartitipantsTask();
		executorService.execute(task);
		return task.call();
	}

	@Override
	public List<ClusterMember> getMembers() {
		return null;
	}

	@Override
	public ClusterState getState() {
		return clusterState;
	}

	@Override
	public void setEventService(ClusterEventService service) {
		this.eventService = service;
	}
	
	@Override
	public ClusterEventService getEventService() {
		return eventService;
	}

	public GossipMessage handleGossip(GossipMessage message) {
		if(message.getCluster().equals(clusterId)) {
			if( message.getTo().equals(memberId) ) {
				HandleGossipMessage task = new HandleGossipMessage(message);
				executorService.execute(task);
				return task.call();
			} else {
				logger.error("Got a gossip message that was intended for someone else: "+message.getTo());
			}
		} else {
			logger.error("Got a gossip message that was intended for another cluster: "+message.getCluster());
		}
		return null;
	}

	public GossipMessage createGossipMessage(ClusterMember to) {
		if(to.getId().equals(memberId)) {
			logger.error("Sending a message to myself? Why?");
		}
		GenerateMessageTask task = new GenerateMessageTask(to);
		executorService.execute(task);
		return task.call();
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	private GossipMessage generateMessage(ClusterMember to) {
		GossipMessage reply = new GossipMessage(clusterId, memberId, to.getId());
		List<GossipClusterMember> members = new ArrayList<GossipClusterMember>(activeMembers.values());
		GossipClusterMember me = new GossipClusterMember(memberId, host, port, System.currentTimeMillis(), clusterStateHash);
		members.add(me);
		clusterStateHash = hashProvider.hashCluster(members);
		me.setHash(clusterStateHash);
		reply.setMemberInfo(members);
		return reply;
	}
	
	private void checkStability() {
		CheckStabilityTask task = new CheckStabilityTask();
		executorService.execute(task);
	}
	
	private void checkDeadMembers() {
		CheckDeadMembersTask task = new CheckDeadMembersTask();
		executorService.execute(task);
	}
	
	public void notifyNewInactive(final GossipClusterMember member) {
		logger.debug("New inactive member "+member.getId());
		eventService.notifyNewInactiveMember(member);
	}
	
	public void notifyNewActive(final GossipClusterMember member) {
		logger.debug("New active member "+member.getId());
		eventService.notifyNewActiveMember(member);
	}
	
	public void notifyMemberDeactivated(final GossipClusterMember member) {
		logger.debug("Deactivated member "+member.getId());
		eventService.notifyMemberDeactivated(member);
	}
	
	public void notifyMemberActivated(final GossipClusterMember member) {
		logger.debug("Activated member "+member.getId());
		eventService.notifyMemberActivated(member);
	}
	
	public void notifyStable(final boolean stable) {
		if(stable) {
			logger.debug("Cluster view stable");
			eventService.notifyClusterStabilized();
		} else {
			logger.debug("Cluster view unstable");
			eventService.notifyClusterDestabilized();
		}
	}


	
	class GetActivePartitipantsTask extends GossipClusterTask<List<ClusterMember>> {

		@Override
		public void run() {
			setResult(new ArrayList<ClusterMember>(activeMembers.values()));
		}
		
	}

	class GetInactivePartitipantsTask extends GossipClusterTask<List<ClusterMember>> {

		@Override
		public void run() {
			setResult(new ArrayList<ClusterMember>(passiveMembers.values()));
		}
		
	}

	class GenerateMessageTask extends GossipClusterTask<GossipMessage> {

		private final ClusterMember to;

		public GenerateMessageTask(ClusterMember to) {
			this.to = to;
		}
		
		@Override
		public void run() {
			setResult(generateMessage(to));
		}
		
	}
	
	class CheckStabilityPeriodicaly implements Runnable {

		@Override
		public void run() {
			checkDeadMembers();
		}
		
	}
	
	class CheckStabilityTask implements Runnable {

		@Override
		public void run() {
			boolean stable = true;
			for(GossipClusterMember member : activeMembers.values()) {
				if(!member.getHash().equals(clusterStateHash)) {
					stable = false;
					if(logger.isDebugEnabled()) {
						logger.debug("Cluster not stable, "+member.getId()+" has a different view.");
					}
				}
			}
			if(clusterState.equals(ClusterState.UNSTABLE) && stable) {
				clusterState = ClusterState.STABLE;
				notifyStable(true);
			}
			else if(clusterState.equals(ClusterState.STABLE) && !stable) {
				clusterState = ClusterState.UNSTABLE;
				notifyStable(false);
			}
		}
		
	}
	
	class CheckDeadMembersTask extends CheckStabilityTask {
		
		@Override
		public void run() {
			boolean stable = true;
			for(GossipClusterMember member : activeMembers.values()) {
				boolean active = System.currentTimeMillis() - member.getLastSeenOnline() < DEADNODE_DELAY;
				if(!active) {
					activeMembers.remove(member.getId());
					passiveMembers.put(member.getId(), member);
					notifyMemberDeactivated(member);
					stable = false;
				}
			}
			if(clusterState.equals(ClusterState.STABLE) && !stable) {
				clusterState = ClusterState.UNSTABLE;
				notifyStable(false);
			}
			super.run();
		}
	}
	
	class HandleGossipMessage extends GossipClusterTask<GossipMessage> {

		private final GossipMessage message;

		public HandleGossipMessage(GossipMessage message) {
			this.message = message;
		}
		

		@Override
		public void run() {
			for(GossipClusterMember member : message.getMemberInfo()) {
				if(member.getId().equals(memberId)) {
					//Skip self
					continue;
				}
				boolean active = System.currentTimeMillis() - member.getLastSeenOnline() < DEADNODE_DELAY;
				if(activeMembers.containsKey(member.getId())) {
					GossipClusterMember existing = activeMembers.get(member.getId());
					if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
						if(active) {
							activeMembers.put(member.getId(), member);
						} else {
							activeMembers.remove(member.getId());
							passiveMembers.put(member.getId(), member);
							notifyMemberDeactivated(member);
						}
					} else {
						boolean existingActive = System.currentTimeMillis() - existing.getLastSeenOnline() < DEADNODE_DELAY;
						if(!existingActive) {
							activeMembers.remove(existing.getId());
							passiveMembers.put(existing.getId(), existing);
							notifyMemberDeactivated(existing);
						}
					}
				} else if(passiveMembers.containsKey(member.getId())) {
					GossipClusterMember existing = passiveMembers.get(member.getId());
					if(existing.getLastSeenOnline() < member.getLastSeenOnline()) {
						if(active) {
							passiveMembers.remove(member.getId());
							activeMembers.put(member.getId(), member);
							notifyMemberActivated(member);
						} else {
							passiveMembers.put(member.getId(), member);
						}
					}
				} else {
					if(active) {
						activeMembers.put(member.getId(), member);
						notifyNewActive(member);
					} else {
						passiveMembers.put(member.getId(), member);
						notifyNewInactive(member);
					}
				}
			}
			setResult(generateMessage(activeMembers.get(message.getFrom())));
			checkStability();
		}


		
	}

}
