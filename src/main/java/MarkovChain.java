import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

class MarkovChain {
	// Hashmap
	private final Hashtable<String, Vector<String>> markovChain = new Hashtable<>();
	private final Random rnd = new Random();

	/**
	 * Initialize empty chain
	 */
	public MarkovChain() {
		createStarterEntries();
	}

	/**
	 * Initialize chain with lines
	 */
	public MarkovChain(LineGetter lineGetter) {
		createStarterEntries();
		addMoreLines(lineGetter);
	}

	public void createStarterEntries() {
		// Create the first two entries (k:_start, k:_end)
		markovChain.put("_start", new Vector<>());
		markovChain.put("_end", new Vector<>());
	}

	/**
	 * Add more lines to the Markov Chain
	 *
	 * @param lineGetter LineGetter
	 */
	public void addMoreLines(LineGetter lineGetter) {
		while (lineGetter.hasNextLine())
			addWords(lineGetter.getNextLine(), markovChain);
	}

	/**
	 * Black Magic
	 *
	 * @param phrase      String
	 * @param markovChain Hashtable
	 */
	private void addWords(String phrase, Hashtable<String, Vector<String>> markovChain) {
		if (phrase.equals("#") || phrase.equals("")) return;
		// put each word into an array
		String[] words = phrase.split(" ");
		// Loop through each word, check if it's already added
		// if its added, then get the suffix vector and add the word
		// if it hasn't been added then add the word to the list
		// if its the first or last word then select the _start / _end key
		try {
			for (int i = 0; i < words.length; i++) {
				// Add the start and end words to their own
				if (i == 0) {
					Vector<String> startWords = markovChain.get("_start");
					startWords.add(words[i]);
					Vector<String> suffix = markovChain.get(words[i]);
					if (suffix == null) {
						suffix = new Vector<>();
						suffix.add(words[i + 1]);
						markovChain.put(words[i], suffix);
					}
				} else if (i == words.length - 1) {
					Vector<String> endWords = markovChain.get("_end");
					endWords.add(words[i]);
				} else {
					Vector<String> suffix = markovChain.get(words[i]);
					if (suffix == null) {
						suffix = new Vector<>();
						suffix.add(words[i + 1]);
						markovChain.put(words[i], suffix);
					} else {
						suffix.add(words[i + 1]);
						markovChain.put(words[i], suffix);
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.print("Shits broke yo:");
			System.out.println(phrase);
		}
	}

	/**
	 * Generate n sentences
	 *
	 * @param n int
	 */
	public void generateSentences(int n) {
		for (int i = 0; i < n; i++)
			generateSentence();
	}

	/**
	 * Generate and show a sentence from the Markov Chain
	 */
	private void generateSentence() {
		// Vector to hold the phrase
		Vector<String> newPhrase = new Vector<>();
		// String for the next word
		String nextWord = "";
		// Select the first word
		Vector<String> startWords = markovChain.get("_start");
		int startWordsLen = startWords.size();

		while (nextWord.isEmpty()) {
			nextWord = startWords.get(rnd.nextInt(startWordsLen));
		}
		newPhrase.add(nextWord);

		// Keep looping through the words until we've reached the end
		while (nextWord.charAt(nextWord.length() - 1) != '#') {
			Vector<String> wordSelection = markovChain.get(nextWord);
			int wordSelectionLen = wordSelection.size();
			String wordCandidate = wordSelection.get(rnd.nextInt(wordSelectionLen));
			if (!wordCandidate.isEmpty()) {
				nextWord = wordCandidate;
				newPhrase.add(nextWord);
			}
		}
		String out = "";
		for (String s : newPhrase)
			out += s + " ";
		out = out.replace("#", "");
		System.out.println(out + "\n");
	}
}