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

/**
 * @author Geert Vos
 */
public interface ClusterEventListener {

	/**
	 * This event will be called when a new member is discovered in the cluster
	 * that is currently online.
	 * 
	 * @param member The information about the member
	 */
	public void onNewActiveMember(ClusterMember member, List<ClusterMember> members);
	
	/**
	 * This event will be called when a new member is discovered in the cluster
	 * that is currently offline.
	 * 
	 * @param member The information about the member
	 */
	public void onNewInactiveMember(ClusterMember member, List<ClusterMember> members);
	
	/**
	 * An already known member changes state from offline to online
	 * 
	 * @param member The information about the member
	 */
	public void onMemberActivated(ClusterMember member, List<ClusterMember> members);
	
	/**
	 * An already known member changes state from online to offline
	 * 
	 * @param member The information about the member
	 */
	public void onMemberDeactivated(ClusterMember member, List<ClusterMember> members);
	
	/**
	 * This event will be called when all active members of the cluster see 
	 * the same list of active members in the cluster.
	 *  
	 * @param members The members in the cluster
	 */
	public void onClusterStabilized(List<ClusterMember> members);
	
	/**
	 * This event will be called when the cluster is destabilized. This means
	 * that the cluster no longer has one shared view of the active members.
	 * This happens when new members are added or when existing members leave.
	 */
	public void onClusterDestabilized(List<ClusterMember> members);
	
}
