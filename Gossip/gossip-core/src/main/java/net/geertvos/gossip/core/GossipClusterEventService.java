package net.geertvos.gossip.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterEventService;
import net.geertvos.gossip.api.cluster.ClusterMember;

public class GossipClusterEventService implements ClusterEventService{

	private final ScheduledThreadPoolExecutor eventListenersExecutorService = new ScheduledThreadPoolExecutor(1);
	private final List<ClusterEventListener> listeners = new CopyOnWriteArrayList<ClusterEventListener>();
	
	@Override
	public void registerListener(ClusterEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(ClusterEventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void notifyNewActiveMember(final ClusterMember member) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewInactiveMember(member);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyNewInactiveMember(final ClusterMember member) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewInactiveMember(member);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyMemberActivated(final ClusterMember member) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberActivated(member);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyMemberDeactivated(final ClusterMember member) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberDeactivated(member);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyClusterStabilized() {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onClusterStabilized();
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyClusterDestabilized() {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onClusterDestabilized();
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

}
