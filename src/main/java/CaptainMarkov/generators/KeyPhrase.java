package CaptainMarkov.generators;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

import java.util.ArrayList;

public class KeyPhrase implements Generator {
	//TODO:Add support for regex to make it possible to get all types of captains logs
	//Enterprise uses "Captain's starlog" most of the time, and there are also many other kinds of logs
	//That it would be fun to include.
	private final String keyPhrase;
	private final boolean cutToPhrase;
	private Series series;
	public MarkovChain chain;


	/**
	 * Constructor for KeyPhrase
	 *
	 * @param phrase      String
	 * @param cutToPhrase boolean
	 * @param series      Series
	 */
	public KeyPhrase(String phrase, boolean cutToPhrase, Series series) {
		keyPhrase = phrase;
		this.cutToPhrase = cutToPhrase;
		this.series = series;
		buildChain();
	}

	/**
	 * Constructor for KeyPhrase for all series
	 *
	 * @param cutToPhrase boolean
	 * @param phrase      String
	 */
	public KeyPhrase(String phrase, boolean cutToPhrase) {
		keyPhrase = phrase;
		this.cutToPhrase = cutToPhrase;
		ArrayList<Series> shows = new ArrayList<>();
		shows.add(Series.TOS);
		shows.add(Series.TNG);
		shows.add(Series.DS9);
		shows.add(Series.VOY);
		shows.add(Series.ENT);
		buildChains(shows);
	}

	/**
	 * Constructor for KeyPhrase for given series
	 *
	 * @param shows       ArrayList
	 * @param cutToPhrase boolean
	 * @param phrase      String
	 */
	public KeyPhrase(String phrase, boolean cutToPhrase, ArrayList<Series> shows) {
		this.cutToPhrase = cutToPhrase;
		this.keyPhrase = phrase;
		chain = null;
		buildChains(shows);
	}

	private void buildChains(ArrayList<Series> shows) {
		for (Series series : shows) {
			if (chain == null) {
				this.series = series;
				buildChain();
			} else {
				merge(new KeyPhrase(keyPhrase, cutToPhrase, series));
			}
		}
	}

	public String generate() {
		return chain.generateSentence();
	}

	public void merge(KeyPhrase that) {
		this.chain.merge(that.chain);
	}

	/**
	 * Save lines matching this keyword
	 */
	public void buildChain() {
		chain = new MarkovChain();
		SeriesReader seriesReader = new SeriesReader(series);
		for (String curr : seriesReader) {
			//Remove parentheses and brackets
			curr = curr.replaceAll("\\(.*?\\)", "");
			curr = curr.replaceAll("\\[.*?\\]", "");
			curr = curr.trim();
			if (curr.contains(keyPhrase)) {
				System.out.println(curr);
				if (cutToPhrase) curr = curr.substring(curr.indexOf(keyPhrase));
				String out = curr.trim() + "#";
				if (!out.equals("#")) {
					chain.addWords(out.trim());
				}
			}
		}
	}
}
