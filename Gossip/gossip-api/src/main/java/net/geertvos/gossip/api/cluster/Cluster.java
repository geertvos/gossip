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
public interface Cluster {

	/**
	 * Get the cluster member of the local host.
	 * @return
	 */
	public ClusterMember getLocalMember();
	
	/**
	 * Get all members marked as active at the time of calling this method.
	 * @return
	 */
	public List<ClusterMember> getActiveMembers();
	
	/**
	 * Get all passive members at the time of calling this method.
	 * @return
	 */
	public List<ClusterMember> getPassiveMembers();
	
	/**
	 * Returns the state of the current cluster. Can be unstable or stable.
	 * @return ClusterState 
	 */
	public ClusterState getState();
	
	/**
	 * Set the service responsible for handling event fired by the service.
	 * @param service
	 */
	public void setEventService(ClusterEventService service);
	
	/**
	 * Get the event service.
	 * @return
	 */
	public ClusterEventService getEventService();
	
}
