package CaptainMarkov.generators;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.IterableFile;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

import java.io.FileNotFoundException;
import java.util.HashSet;

public class Treknobabble implements Generator {
	public MarkovChain chain;
	private HashSet<String> dictionary;

	public Treknobabble(double threshold) {
		chain = new MarkovChain();
		SeriesReader seriesReader = new SeriesReader(Series.TOS);
		loadDictionary();
		while (seriesReader.hasNextSeries()) {
			ChainBuilder.THIS.setLabel("Reading series " + seriesReader.getSeries().toString());
			for (String cur : seriesReader) {
				cur = cur.replaceFirst("[A-Z]+:", "");
				if (getCoherencyRatio(cur) < threshold)
					chain.addWords(cur + "#");
			}
			seriesReader.nextSeries();
		}
	}

	public String generate() {
		return chain.generateSentence();
	}

	/**
	 * Load dictionary from file into HashSet
	 */
	private void loadDictionary() {
		try {
			IterableFile file = new IterableFile("dictionary.txt");
			dictionary = new HashSet<>();
			for (String s : file) {
				dictionary.add(s);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find dictionary.txt");
			e.printStackTrace();
		}
	}

	/**
	 * Uses an English dictionary to rate input sentences for how many fake words they contain.
	 * Only accept sentences that are below a (yet to be determined) threshold
	 * Possibly 50%, AKA a threshold of 1
	 *
	 * @param dialogue String
	 */
	private int getCoherencyRatio(String dialogue) {
		int trekWords = 0;
		int realWords = 0;
		String[] words = dialogue.split(" ");
		for (String word : words) {
			if (dictionary.contains(word)) realWords++;
			else trekWords++;
		}
		if(trekWords == 0) return 100;
		return realWords/trekWords;
	}
}
