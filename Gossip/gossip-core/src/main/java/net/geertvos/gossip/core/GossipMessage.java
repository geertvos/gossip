package net.geertvos.gossip.core;

import java.util.List;

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
