package com.spellChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.server.PathParam;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.config.SolrConnection;
import com.getSearchData.SearchData;

@Controller
public class AutoSpellSuggest {

	@RequestMapping("/")
	String get() {
		return "search";
	}

	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	public ModelAndView suggestSpell(@PathParam(value = "text") String text, Model model) {
		SolrConnection solrConnection = new SolrConnection();
		HttpSolrClient client = solrConnection.getSolrConnection();

		SolrQuery query = new SolrQuery();
		
		query.setRequestHandler("/suggest");
		query.add("fl", "*,score");

		HashMap<String, List<String>> map = new HashMap<>();
		HashMap<String, List<String>> autoSuggestionWord = new HashMap<>();
		query.add("q", text);
		ModelAndView view = new ModelAndView();
		try {
			QueryResponse response = client.query(query);
			System.out.println(response);
			map = getResult(response);
			autoSuggestionWord.putAll(map);
			SpellMatching spellMatching = new SpellMatching();
			SearchData searchData = new SearchData();
			
			String fixedText = text;
			if (map.size() != 0) {
				fixedText = fixSentence(text, map);
				
			} 
			
			HashMap<String, List<String>> afterSpellCheckWord = new HashMap<>();
			
			afterSpellCheckWord = spellMatching.SpellCheck(fixedText); //Geting all suggest word after check spell
			fixedText = afterSpellCheckWord.get("correctSentence").get(0);
		
			afterSpellCheckWord.remove("correctSentence");
			autoSuggestionWord.putAll(afterSpellCheckWord);
		
			
			/**
			 * Now search with correct sentence
			 */
		
			List<HashMap<String, String>> searchResult = new ArrayList();
			searchResult = searchData.suggestSpell(fixedText);
			
			//System.out.println("---------------data : "+searchData);
			
			if(autoSuggestionWord.size()!=0) {
				String correctUrlSearch = "/suggest?text=" + fixedText;
				String wrongUrlSearch = "/suggest?text=" + text;
				view.addObject("correctWordUrl", correctUrlSearch);
				view.addObject("wrongWordUrl", wrongUrlSearch);
				view.addObject("suggest", fixedText);
				view.addObject("token", text);
			}
			
			String searchNotFound;
			if(searchResult.size() !=0) {
				view.addObject("data", searchResult);
				searchNotFound = null;
			}else {
				searchNotFound = "NOT FOUND";
				view.addObject("searchNotFound", searchNotFound);
				System.out.println(text);
				view.addObject("token", text);
				view.addObject("suggest", null);
				view.addObject("token", null);
				view.addObject("data",null);
			}
			view.setViewName("search");
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	
//Method for find correct Word
	HashMap<String, List<String>> getResult(QueryResponse response) {
		SolrDocumentList list = response.getResults();
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();

		HashMap<String, List<String>> map = new HashMap<>();

		for (Suggestion suggestion : spellCheckResponse.getSuggestions()) {
			System.out.println("hello");
			
			map.put(suggestion.getToken(), suggestion.getAlternatives());
		/*	System.out.println("Original Frequency : " + suggestion.getOriginalFrequency());
			System.out.println("Alternatives Frequency : " + suggestion.getAlternativeFrequencies());
			System.out.println("No. found : " + suggestion.getNumFound());
			System.out.println("Original Frequency : " + suggestion.getStartOffset());
			System.out.println("original token: " + suggestion.getToken() + " - alternatives: " + suggestion.getAlternatives());
		*/}

		return map;

	}

	//Method for making corect sentence
	String fixSentence(String text, HashMap<String, List<String>> map) {
		String arr[] = text.split(" ");
		for (String key : map.keySet()) {
			text = text.replaceAll("\\b" + key + "\\b", map.get(key).get(0));
		}
		return text;
	}

}
