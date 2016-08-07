package com.flatironschool.javacs;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.URL;
import redis.clients.jedis.Jedis;


public class FuzzySearch {
  private final HashMap<String, Integer> nWords = new HashMap<String, Integer>();

  public FuzzySearch() throws IOException {
    String slash = File.separator;
    String filename = "resources" + slash + "dict.txt";
    URL fileURL = FuzzySearch.class.getClassLoader().getResource(filename);

    try {
    BufferedReader in = new BufferedReader(new FileReader(fileURL.getFile()));
    Pattern p = Pattern.compile("\\w+");
    for(String temp = ""; temp != null; temp = in.readLine()){
      Matcher m = p.matcher(temp.toLowerCase());
      while(m.find()) nWords.put((temp = m.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
    }
    in.close();
  }
  catch (FileNotFoundException e) {
    System.out.println("There was an error in reading the dictionary");
  }
}

  private final ArrayList<String> edits(String word) {
    ArrayList<String> result = new ArrayList<String>();
    for(int i=0; i < word.length(); ++i) {
      result.add(word.substring(0, i) + word.substring(i+1));
    }
    for(int i=0; i < word.length()-1; ++i) {
       result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
     }
    for(int i=0; i < word.length(); ++i) {
      for(char c='a'; c <= 'z'; ++c) {
        result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
      }
    }
    for(int i=0; i <= word.length(); ++i) {
      for(char c='a'; c <= 'z'; ++c) {
        result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
      }
    }
    return result;
  }

  public final String correct(String word) {
    if(nWords.containsKey(word)) return word;
    ArrayList<String> list = edits(word);
    HashMap<Integer, String> candidates = new HashMap<Integer, String>();
    for(String s : list) {
      if(nWords.containsKey(s)) {
        candidates.put(nWords.get(s),s);
      }
    }
    if(candidates.size() > 0) {
      return candidates.get(Collections.max(candidates.keySet()));
    }
    for(String s : list) {
      for(String w : edits(s)) {
        if(nWords.containsKey(w)) {
          candidates.put(nWords.get(w),w);
        }
      }
    }
    return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
  }

}
