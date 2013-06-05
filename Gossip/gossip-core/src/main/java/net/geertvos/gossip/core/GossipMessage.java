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

/**
 * @author Geert Vos
 */

public class GossipMessage {

	private List<GossipClusterMember> memberInfo;
	private String from;
	private String to;
	private String cluster;
	
	public GossipMessage() {
	}
	
	public GossipMessage(String cluster, String from, String to) {
		this.from = from;
		this.to = to;
		this.cluster = cluster;
	}
	
	public List<GossipClusterMember> getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(List<GossipClusterMember> memberInfo) {
		this.memberInfo = memberInfo;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	
}
