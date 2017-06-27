package com.twopc.github.canal2other.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.twopc.github.canal2other.entity.NodeInfo;



public class EsClient {
	
    private static Logger logger = Logger.getLogger(EsClient.class);
	
	public EsClient(String clusterName,Set<NodeInfo> nodeInfos){
		Settings settings = Settings.builder().put("client.transport.sniff", true).put("cluster.name", clusterName).build();
		TransportClient client = new PreBuiltTransportClient(settings);
		for (NodeInfo nodeInfo : nodeInfos) {
			try {
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(nodeInfo.getIp()),nodeInfo.getPort()));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
