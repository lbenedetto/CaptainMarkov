import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class MarkovChain {
	// Hashmap
	Hashtable<String, Vector<String>> markovChain = new Hashtable<>();
	Random rnd = new Random();

	public MarkovChain(LineGetter lineGetter) {
		// Create the first two entries (k:_start, k:_end)
		markovChain.put("_start", new Vector<>());
		markovChain.put("_end", new Vector<>());
		while (lineGetter.hasNextLine())
			addWords(lineGetter.getNextLine(), markovChain);
	}

	public void addWords(String phrase, Hashtable<String, Vector<String>> markovChain) {
		if (phrase.equals("#") || phrase.equals("")) return;
		// put each word into an array
		String[] words = phrase.split(" ");
		// Loop through each word, check if it's already added
		// if its added, then get the suffix vector and add the word
		// if it hasn't been added then add the word to the list
		// if its the first or last word then select the _start / _end key
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
	}

	public void generateSentence() {
		// Vector to hold the phrase
		Vector<String> newPhrase = new Vector<>();
		// String for the next word
		String nextWord;
		// Select the first word
		Vector<String> startWords = markovChain.get("_start");
		int startWordsLen = startWords.size();
		nextWord = startWords.get(rnd.nextInt(startWordsLen));
		newPhrase.add(nextWord);
		// Keep looping through the words until we've reached the end
		while (nextWord.charAt(nextWord.length() - 1) != '#') {
			Vector<String> wordSelection = markovChain.get(nextWord);
			int wordSelectionLen = wordSelection.size();
			nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
			newPhrase.add(nextWord);
		}
		showPhrase(newPhrase);
	}

	public void showPhrase(Vector<String> phrase) {
		String out = "";
		for (String s : phrase) {
			out += s + " ";
		}
		out = out.replace("#", "");
		System.out.println(out + " " + out.length() + " characters");
	}
}