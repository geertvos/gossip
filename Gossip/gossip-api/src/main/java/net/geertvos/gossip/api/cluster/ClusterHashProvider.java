package net.geertvos.gossip.api.cluster;

import java.util.Collection;


public interface ClusterHashProvider<T extends ClusterMember> {

	String hashCluster(Collection<T> members);
	
}
