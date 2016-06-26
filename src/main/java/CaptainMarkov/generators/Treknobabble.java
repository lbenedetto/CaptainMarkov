package CaptainMarkov.generators;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.IterableFile;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

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
		dictionary = new HashSet<>();
		InputStream in = this.getClass().getResourceAsStream("resources/dictionary.txt");
		if (in == null) {
			legacyLoadDictionary();
			return;
		}
		Scanner scanner = new Scanner(in);
		String s = null;
		while (scanner.hasNextLine()) {
			s = scanner.nextLine();
			dictionary.add(s);
		}
	}

	/**
	 * Load dictionary with old method to support running program from IDE instead of from JAR
	 */
	private void legacyLoadDictionary() {
		try {
			IterableFile file = new IterableFile("src/main/java/CaptainMarkov/generators/resources/dictionary.txt");
			for (String s : file) {
				dictionary.add(s);
			}
		} catch (FileNotFoundException e) {
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
		if (trekWords == 0) return 100;
		return realWords / trekWords;
	}
}
