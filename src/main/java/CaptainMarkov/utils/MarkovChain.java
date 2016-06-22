package CaptainMarkov.utils;

import CaptainMarkov.gui.ChainBuilder;

import java.util.*;

public class MarkovChain {
	// Hashmap
	private Hashtable<String, Vector<String>> chain = new Hashtable<>();
	private final Random rand = new Random();

	/**
	 * Initialize empty chain
	 */
	public MarkovChain() {
		createStarterEntries();
	}

	/**
	 * Initialize chain with lines
	 */
	public MarkovChain(IterableFile lines) {
		createStarterEntries();
		addFile(lines);
	}

	private void createStarterEntries() {
		// Create the first two entries (k:_start, k:_end)
		chain.put("_start", new Vector<>());
		chain.put("_end", new Vector<>());
	}

	/**
	 * Add more lines to the Markov Chain
	 *
	 * @param lines IterableFile
	 */
	public void addFile(IterableFile lines) {
		for (String s : lines)
			addWords(s);
	}

	/**
	 * Merge a MarkovChain with another MarkovChain
	 * Pretty sure that this works...
	 *
	 * @param that MarkovChain
	 */
	public void merge(MarkovChain that) {
		Hashtable<String, Vector<String>> thatChain = that.chain;
		Hashtable<String, Vector<String>> newChain = new Hashtable<>();
		Set<String> thisKeys = chain.keySet();
		Set<String> thatKeys = thatChain.keySet();
		Set<String> keys = new HashSet<>();
		keys.addAll(thisKeys);
		keys.addAll(thatKeys);
		for (String key : keys) {
			Vector<String> newPair = new Vector<>();
			Vector<String> thisPair = chain.get(key);
			Vector<String> thatPair = thatChain.get(key);
			if (thisPair != null) newPair.addAll(thisPair);
			if (thatPair != null) newPair.addAll(thatPair);
			chain.remove(key);
			thatChain.remove(key);
			newChain.put(key, newPair);
		}

		chain = newChain;
	}

	/**
	 * Splits the phrase into groups of two, with overlap
	 * This is for achieving a chain length of three
	 *
	 * @param phrase String
	 * @return String[]
	 */
	private String[] spliterator(String phrase) {
		String[] split = phrase.split(" ");
		String[] out = new String[split.length];
		for (int ix = 1; ix < split.length; ix++) {
			String temp = split[ix - 1];
			temp += " " + split[ix];
			out[ix - 1] = temp;
		}
		return out;
	}

	/**
	 * Removes every other word in the phrase
	 *
	 * @param phrase String
	 * @return String
	 */
	private String removeDuplicateWords(String phrase) {
		String[] words = phrase.split(" ");
		String out = "";
		boolean add = true;
		for (String s : words) {
			if (add) {
				out += s + " ";
				add = false;
			} else {
				add = true;
			}
		}
		if (words.length % 2 == 0)
			return out + words[words.length - 1];
		return out;

	}

	/**
	 * Add words from phrase to chain
	 *
	 * @param phrase String
	 */

	public void addWords(String phrase) {
		if (phrase.equals("#") || phrase.equals("")) return;
		// put each word into an array
		String[] words = spliterator(phrase);
		//No point in adding 2 word phrases
		if (words.length < 2) return;
		// Loop through each word, check if it's already added
		// if its added, then get the suffix vector and add the word
		// if it hasn't been added then add the word to the list
		// if its the first or last word then select the _start / _end key
		for (int i = 0; i < words.length; i++) {
			if (i == 0) {
				//Add the first part of the phrase as a suffix for _start
				Vector<String> startWords = chain.get("_start");
				startWords.add(words[i]);
				Vector<String> suffix = chain.get(words[i]);
				if (suffix == null) {
					suffix = new Vector<>();
					suffix.add(words[i + 1]);
					chain.put(words[i], suffix);
				}
			} else if (i == words.length - 1) {
				//Add the last part of the phrase as a suffix for _end
				Vector<String> endWords = chain.get("_end");
				endWords.add(words[i]);
			} else {
				//Add the current part of the phrase to the appropriate list of suffixes
				Vector<String> suffix = chain.get(words[i]);
				if (suffix == null) {
					suffix = new Vector<>();
					suffix.add(words[i + 1]);
					chain.put(words[i], suffix);
				} else {
					suffix.add(words[i + 1]);
					chain.put(words[i], suffix);
				}
			}
		}
	}

	/**
	 * Generate and show a sentence from the Markov Chain
	 */
	public String generateSentence() {
		// String for the next word
		String nextWord = "";
		// Select the first word
		Vector<String> startWords = chain.get("_start");
		int startWordsLen = startWords.size();
		if (startWordsLen == 0) {
			String error = "The most likely cause is that the phrases you tried to use never occurred in Star Trek";
			System.out.println(error);
			ChainBuilder.THIS.setLabel(error);
		}
		while (nextWord.isEmpty()) {
			nextWord = startWords.get(rand.nextInt(startWordsLen));
		}
		return generateSentenceWithSeed(nextWord);
	}

	public String generateSentenceWithSeed(String seed) {

		// Vector to hold the phrase
		Vector<String> newPhrase = new Vector<>();
		newPhrase.add(seed);
		String nextWord = seed;
		// Keep looping through the words until we've reached the end
		while (nextWord.charAt(nextWord.length() - 1) != '#') {
			Vector<String> wordSelection = chain.get(nextWord);
			String wordCandidate = null;
			if (wordSelection == null) System.out.println("Couldn't find seed in chain");
			int attempts = 0;
			while (wordCandidate == null && attempts <= 10) {
				int ix = rand.nextInt(wordSelection.size());
				wordCandidate = wordSelection.get(ix);
				attempts++;
			}
			if (wordCandidate == null) {
				System.out.println("Couldn't find next word candidate");
				break;
			}
			if (!wordCandidate.isEmpty()) {
				nextWord = wordCandidate;
				newPhrase.add(nextWord);
			}
		}
		String out = "";
		for (String s : newPhrase)
			out += s + " ";
		out = out.replace("#", "");
		out = removeDuplicateWords(out);
		return out;
	}
}