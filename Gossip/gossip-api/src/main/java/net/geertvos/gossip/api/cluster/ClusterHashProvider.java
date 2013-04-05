package net.geertvos.gossip.api.cluster;

import java.util.Collection;


public interface ClusterHashProvider {

	String hashCluster(Collection<ClusterMember> members);
	
}
