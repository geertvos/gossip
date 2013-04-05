package net.geertvos.gossip.core;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.geertvos.gossip.api.cluster.Cluster;
import net.geertvos.gossip.api.cluster.ClusterHashProvider;
import net.geertvos.gossip.api.cluster.ClusterMember;

public class Md5HashProvider implements ClusterHashProvider {

	private final ClusterMemberComperator comperator = new ClusterMemberComperator();
	private final MessageDigest digest;
	private final Charset charset;
	
	public Md5HashProvider() {
		try { 
			digest = MessageDigest.getInstance("MD5");
			charset = Charset.forName("UTF-8");
		} catch(Exception e) {
			throw new IllegalStateException("Unable to instantiate digest or charset.",e);
		}
	}
	
	public class ClusterMemberComperator implements Comparator<ClusterMember> {

		@Override
		public int compare(ClusterMember arg0, ClusterMember arg1) {
			return arg0.getId().compareTo(arg1.getId());
		}
		
	}

	@Override
	public String hashCluster(Collection<ClusterMember> members) {
		List<ClusterMember> sortedMembers = new ArrayList(members);
		Collections.sort(sortedMembers, comperator);
		
		StringBuilder string = new StringBuilder();
		for(ClusterMember member : sortedMembers) {
			string.append(member.getId()+"|");
		}
		
		return new String(digest.digest(string.toString().getBytes(charset)), charset);
	}
	
}
