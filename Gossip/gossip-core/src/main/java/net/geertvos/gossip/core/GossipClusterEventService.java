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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.geertvos.gossip.api.cluster.ClusterEventListener;
import net.geertvos.gossip.api.cluster.ClusterEventService;
import net.geertvos.gossip.api.cluster.ClusterMember;
/**
 * The GossipClusterEventService provides a threadsafe implementation of the event service.
 * All event handlers will be called on the same thread as the first call and in-order.
 * 
 * @author Geert Vos
 */
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
	public void notifyNewActiveMember(final ClusterMember member, final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewActiveMember(member, members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyNewInactiveMember(final ClusterMember member,final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onNewInactiveMember(member, members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyMemberActivated(final ClusterMember member, final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberActivated(member, members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyMemberDeactivated(final ClusterMember member, final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onMemberDeactivated(member, members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyClusterStabilized(final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onClusterStabilized(members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

	@Override
	public void notifyClusterDestabilized(final List<ClusterMember> members) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(ClusterEventListener listener : listeners) {
					listener.onClusterDestabilized(members);
				}
			}
		};
		eventListenersExecutorService.execute(task);
	}

}
