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
package net.geertvos.gossip.api.cluster;

import java.util.List;

import net.geertvos.gossip.api.cluster.ClusterEventListener;

/**
 * @author Geert Vos
 */
public interface ClusterEventService {

	void registerListener(ClusterEventListener listener);
	
	void unregisterListener(ClusterEventListener listener);
	
	public void notifyNewActiveMember(ClusterMember member, List<ClusterMember> members);
	
	public void notifyNewInactiveMember(ClusterMember member, List<ClusterMember> members);
	
	public void notifyMemberActivated(ClusterMember member, List<ClusterMember> members);
	
	public void notifyMemberDeactivated(ClusterMember member, List<ClusterMember> members);
	
	public void notifyClusterStabilized(List<ClusterMember> members);
	
	public void notifyClusterDestabilized(List<ClusterMember> members);
	
}
