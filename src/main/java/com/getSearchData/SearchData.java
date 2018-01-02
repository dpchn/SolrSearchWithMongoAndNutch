package com.getSearchData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import com.config.SolrConnection;

public class SearchData {

	public List<HashMap<String, String>> suggestSpell(String text) {
		SolrConnection solrConnection = new SolrConnection();
		HttpSolrClient client = solrConnection.getSolrConnection();

		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/select");

		List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
		query.add("q", text);
		query.add("fl", "*,score");
		try {
			QueryResponse response = client.query(query);
			dataList = getData(response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	List<HashMap<String, String>> getData(QueryResponse response) {
		
		SolrDocumentList list = response.getResults();
		HashMap<String, List<HashMap<String, String>>> map = new HashMap<>();
		
		List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
		
		for (SolrDocument doc : list) {
			HashMap<String, String> dataMap = new HashMap<>();
			for (String str : doc.getFieldNames()) {
				if(str.equals("content") || str.equals("title") || str.equals("url"))
					dataMap.put(str, doc.getFieldValue(str).toString());
				//	System.out.println(str+" : "+doc.getFieldValue(str));
			}
			dataList.add(dataMap);
		}
		map.put("data", dataList);
		return dataList;
	}
}
