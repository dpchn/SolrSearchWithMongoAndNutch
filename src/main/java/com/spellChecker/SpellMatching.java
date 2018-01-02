package com.spellChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.config.SolrConnection;

@Controller
public class SpellMatching {

	/*
	 * @RequestMapping("/spell") public ModelAndView SpellCheck(@PathParam(value =
	 * "text") String text) { String url = "http://localhost:8983/solr/Query_Demo/";
	 * HttpSolrClient client = new HttpSolrClient(url); client.setParser(new
	 * XMLResponseParser()); ModelAndView view = new ModelAndView(); HashMap<String,
	 * List<String>> map = new HashMap<>(); try { SolrQuery query = new SolrQuery();
	 * query.setRequestHandler("/spell"); ModifiableSolrParams params = new
	 * ModifiableSolrParams(); params.set("qt", "/spell"); params.set("q", text);
	 * params.set("spellcheck", "true"); params.set("spellcheck.build", "true");
	 * QueryResponse queryResponse = client.query(params);
	 * view.setViewName("search"); map = printResult(queryResponse); if (map.size()
	 * != 0) { String fixedText = fixSentence(text, map); String correctUrlSearch =
	 * "/spell?text=" + fixedText; String wrongUrlSearch = "/spell?text=" + text;
	 * view.addObject("correctWordUrl", correctUrlSearch);
	 * view.addObject("wrongWordUrl", wrongUrlSearch); view.addObject("suggest",
	 * fixedText); view.addObject("token", text); } else { view.addObject("suggest",
	 * null); view.addObject("token", null);
	 * 
	 * } view.addObject("data", printResult(queryResponse));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return view; }
	 */

	public HashMap<String, List<String>> SpellCheck(String text) {
		SolrConnection solrConnection = new SolrConnection();
		HttpSolrClient client = solrConnection.getSolrConnection();
		ModelAndView view = new ModelAndView();
		HashMap<String, List<String>> map = new HashMap<>();
		try {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler("/spell");
			query.add("fl", "*,score");
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("qt", "/spell");
			params.set("q", text);
			params.set("spellcheck", "true");
			params.set("spellcheck.build", "true");
			QueryResponse queryResponse = client.query(params);
			view.setViewName("search");
			map = printResult(queryResponse);
			if (map.size() != 0) {
				text = fixSentence(text, map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		list.add(text);
		map.put("correctSentence", list);
		return map;
	}

	HashMap<String, List<String>> printResult(QueryResponse response) {
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		if (!spellCheckResponse.isCorrectlySpelled()) {
			
			for (Suggestion suggestion : spellCheckResponse.getSuggestions()) {
				
				map.put(suggestion.getToken(), suggestion.getAlternatives());
			}
		}
		return map;
	}

	// Method for making corect sentence
	String fixSentence(String text, HashMap<String, List<String>> map) {
		for (String key : map.keySet()) {
			text = text.replaceAll("\\b" + key + "\\b", map.get(key).get(0));
		}
		return text;
	}
}
