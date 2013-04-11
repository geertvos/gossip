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

import net.geertvos.gossip.api.cluster.ClusterMember;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Geert Vos
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GossipClusterMember implements ClusterMember {

	private String id;
	private String ip;
	private int port;
	private long lastSeenOnline;
	private String hash;

	public GossipClusterMember() {
	}
	
	public GossipClusterMember(String id, String ip, int port, long lastSeenOnline, String hash) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.lastSeenOnline = lastSeenOnline;
		this.hash = hash;
	}

	public GossipClusterMember(String id, String ip, int port, long lastSeenOnline) {
		this(id,ip,port,lastSeenOnline,"");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public long getLastSeenOnline() {
		return lastSeenOnline;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setLastSeenOnline(long lastSeenOnline) {
		this.lastSeenOnline = lastSeenOnline;
	}

}
