package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	private JedisIndex index;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
		try {
			Jedis jedis = JedisMaker.make();
			index = new JedisIndex(jedis);
		} catch (IOException e) {}
	}
	
	/**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer tf = map.get(url);
		if (tf == null) {
			return 0;
		}
		Double idf = java.lang.Math.log(index.size() / map.size());
		Integer relevance = tf * idf.intValue();
		return relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		Map<String, Integer> union = new HashMap<String, Integer>();
		Set<Entry<String, Integer>> unionKeySet = new HashSet<Entry<String, Integer>>(this.sort());
		unionKeySet.addAll(that.sort());
		for (Entry<String, Integer> entry : unionKeySet) {
			String key = entry.getKey();
			int firstValue = this.getRelevance(key);
			int secondValue = that.getRelevance(key);
			if (firstValue != 0 && secondValue != 0) {
				int totalValue = java.lang.Math.max(totalRelevance(firstValue, secondValue), java.lang.Math.max(firstValue, secondValue));
				union.put(key, totalValue);
			} else if (firstValue != 0) {
				union.put(key, firstValue);
			} else {
				union.put(key, secondValue);
			}
		}
		return new WikiSearch(union);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
		Map<String, Integer> intersection = new HashMap<String, Integer>();
		for (String key : map.keySet()) {
			if (that.getRelevance(key) != 0) {
				intersection.put(key, totalRelevance(this.getRelevance(key), that.getRelevance(key)));
			}
		}
		return new WikiSearch(intersection);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		Map<String, Integer> minus = new HashMap<String, Integer>();
		for (String key : map.keySet()) {
			if (that.getRelevance(key) == 0) {
				minus.put(key, this.getRelevance(key));
			}
		}
		return new WikiSearch(minus);
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		return (rel1 + rel2) / 2;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
				int firstValue = getRelevance(entry1.getKey());
				int secondValue = getRelevance(entry2.getKey());
				if (firstValue < secondValue) {
					return 1;
				} else if (firstValue > secondValue) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		Map<String, Integer> map = index.getCountsFaster(term);
		return new WikiSearch(map);
	}

	/*public static void main(String[] args) throws IOException {
		
		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		
		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();
		
		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();
		
		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}*/
}
