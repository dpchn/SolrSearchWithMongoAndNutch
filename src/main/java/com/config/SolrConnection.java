package com.config;

import java.io.IOException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class SolrConnection {
	static HttpSolrClient client;
	static {
		String url = "http://localhost:8983/solr/nutch/";
		client = new HttpSolrClient(url);
		client.setParser(new XMLResponseParser());	
	}
	
	
	
	public HttpSolrClient getSolrConnection() {
		return client;
	}
	
	
	public void closeSolrConnection() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
