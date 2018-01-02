package com.solrConfig;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class SolrServer {
	public HttpSolrClient StartSolrServer() {
		String url = "http://localhost:8983/solr/Query_Demo/";
		HttpSolrClient client = new HttpSolrClient(url);
		client.setParser(new XMLResponseParser());
		return client;
	}
}