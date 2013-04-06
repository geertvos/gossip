package net.geertvos.gossip.core;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.geertvos.gossip.api.cluster.ClusterHashProvider;
import net.geertvos.gossip.api.cluster.ClusterMember;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class Md5HashProvider implements ClusterHashProvider<GossipClusterMember> {

	private final ClusterMemberComperator comperator = new ClusterMemberComperator();
	private final MessageDigest digester = DigestUtils.getMd5Digest();
	private final Charset charset = Charsets.UTF_8;
	
	public class ClusterMemberComperator implements Comparator<GossipClusterMember> {

		@Override
		public int compare(GossipClusterMember arg0, GossipClusterMember arg1) {
			return arg0.getId().compareTo(arg1.getId());
		}
		
	}

	@Override
	public String hashCluster(Collection<GossipClusterMember> members) {
		List<GossipClusterMember> sortedMembers = new ArrayList<GossipClusterMember>(members);
		Collections.sort(sortedMembers, comperator);
		
		StringBuilder stringBuilder = new StringBuilder();
		for(ClusterMember member : sortedMembers) {
			stringBuilder.append(member.getId()+"|");
		}
		
		byte[] bytes = stringBuilder.toString().getBytes(charset);
		byte[] digest = digester.digest(bytes);
		byte[] encoded = Base64.encodeBase64(digest);
		return new String(encoded, charset);
	}
	
}
