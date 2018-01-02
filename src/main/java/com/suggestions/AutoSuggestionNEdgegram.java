package com.suggestions;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class AutoSuggestionNEdgegram {

	public void autoSuggest(HttpSolrClient client) {
		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/suggest_topic");

		query.add("q", "kachaloo");
		try {
			QueryResponse response = client.query(query);
			System.out.println(response);
			printResult(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void printResult(QueryResponse response) {
		SolrDocumentList list = response.getResults();
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
/*
		for (Suggestion suggestion : spellCheckResponse.getSuggestions()) {

			System.out.println("Original Frequency : " + suggestion.getOriginalFrequency());
			System.out.println("Alternatives Frequency : " + suggestion.getAlternativeFrequencies());
			System.out.println("No. found : " + suggestion.getNumFound());
			System.out.println("Original Frequency : " + suggestion.getStartOffset());
			System.out.println(
					"original token: " + suggestion.getToken() + " - alternatives: " + suggestion.getAlternatives());
		}*/
		

		for (SolrDocument doc : list) {
			for (String str : doc.getFieldNames()) {
				System.out.println(str + " : " + doc.getFieldValue(str));
			}

			System.out.println();
		}
		

	}
}
