package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		if (docFile == null) {
			throw new FileNotFoundException();
		}
		HashMap<String, Occurrence> keyMap = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext() == true) {
			String keywords = getKeyword(sc.next());
			if (keywords != null) {
				if (keyMap.containsKey(keywords)) {
					Occurrence occ = keyMap.get(keywords);
					occ.frequency++;
				}
				else {
					Occurrence occ = new Occurrence(docFile, 1);
					keyMap.put(keywords, occ);
				}
			}
		}
		return keyMap;
	}
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(String keyword: kws.keySet()) {
			ArrayList<Occurrence> occ = new ArrayList<>();
			if(keywordsIndex.containsKey(keyword)) {
				occ = keywordsIndex.get(keyword);
			}
			occ.add(kws.get(keyword));
			insertLastOccurrence(occ);
			keywordsIndex.put(keyword, occ);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	private static boolean isACharacter(String w) {
		int count = 0;
		boolean top = false;
		while (count < w.length()) {
			char ch = w.charAt(count);
			if (!(Character.isLetter(ch))) {
				top = true;
			}
			if ((top) && (Character.isLetter(ch))) {
				return true;
			}
			count++;
		}
		return false;
	}
	
	private String removeAfterPunctuation(String word) {
		int i = 0;
		while (i < word.length()) {
			char c = word.charAt(i);
			if (!(Character.isLetter(c))) {
				break;
			}
			i++;
			
		}
		return word.substring(0, i);
	}

	public String getKeyword(String word) {
		if ((word == null) || (word.equals(null))) {
			return null;
		}
		word = word.toLowerCase();
		
		if (isACharacter(word) == true) {
			return null;
		}
		word = removeAfterPunctuation(word);

		if (noiseWords.contains(word)) {
			return null;
		}
		if (word.length() <= 0) {
			return null;
		}
		return word;
	}
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if (occs.size() < 2) {
			return null;
		}
		int lower = 0;
		int higher = occs.size()-2;
		int goal = occs.get(occs.size()-1).frequency;
		int mid = 0;
		ArrayList<Integer> midpts = new ArrayList<Integer>();
		while (higher >= lower) {
			mid = ((lower + higher) / 2);
			int data = occs.get(mid).frequency;
			midpts.add(mid);
			if (data == goal) {
				break;
			}
			else if (data < goal) {
				higher = mid - 1;
			}
			else if (data > goal) {
				lower = mid + 1;
				if (higher <= mid) {
					mid = mid + 1;
				}
			}
		}
		midpts.add(mid);
		Occurrence temp = occs.remove(occs.size()-1);
		occs.add(midpts.get(midpts.size()-1), temp);
		return midpts;
	}
	
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {

		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String file = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(file);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Occurrence> occur1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> occur2 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> combo = new ArrayList<Occurrence>();
		if (keywordsIndex.containsKey(kw1)) {
			occur1 = keywordsIndex.get(kw1);
		}
		if (keywordsIndex.containsKey(kw2)) {
			occur2 = keywordsIndex.get(kw2);
		}
		combo.addAll(occur1);
		combo.addAll(occur2);
		if (!(occur1.isEmpty()) && !(occur2.isEmpty())) {
			for (int x = 0; x < combo.size()-1; x++) {
				for (int y = 1; y < combo.size()-x; y++) { 
					if (combo.get(y-1).frequency < combo.get(y).frequency) {
						Occurrence temp = combo.get(y-1);
						combo.set(y-1, combo.get(y));
						combo.set(y,  temp);
					}
				}
			}
			for (int x = 0; x < combo.size()-1; x++) {
				for (int y = x + 1; y < combo.size(); y++) {
					if (combo.get(x).document == combo.get(y).document) {
						combo.remove(y);
					}
				}
			}
		}
		while (combo.size() > 5) {
			combo.remove(combo.size()-1);
		}
		System.out.println(combo);
		for (Occurrence oc : combo) {
			result.add(oc.document);
		}
		return result;
	}
	
}