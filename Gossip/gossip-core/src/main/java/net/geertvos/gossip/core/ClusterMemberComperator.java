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


import java.util.Comparator;

import net.geertvos.gossip.api.cluster.ClusterMember;

/**
 * Compares cluster members by id
 * 
 * @author Geert Vos
 */
public class ClusterMemberComperator<T extends ClusterMember> implements Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		return arg0.getId().compareTo(arg1.getId());
	}
	
}