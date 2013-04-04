package net.geertvos.gossip.core;

import java.util.List;

public class GossipMessage {

	private List<GossipClusterMember> memberInfo;

	public List<GossipClusterMember> getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(List<GossipClusterMember> memberInfo) {
		this.memberInfo = memberInfo;
	}
	
}
