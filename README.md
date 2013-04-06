Gossip Cluster Framework
======

A Java implementation of a clustering framework based on gossip networks. 

The setup is extremely simple:

Create a cluster with knowledge of at least on other member in the cluster and start the server.

GossipClusterMember otherMember = new GossipClusterMember("2", "otherhost", 8000, System.currentTimeMillis(),"");

GossipCluster cluster = new GossipCluster("1", "localhost", 8000, otherMember);

GossipServer server = new GossipServer(cluster);

server.start();

