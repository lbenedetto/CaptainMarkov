package CaptainMarkov.generators;

import java.util.Random;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

public class CaptainsLog implements Generator {
	public final MarkovChain chain;
	private final String seed;
	private final boolean defaultMode;

	public CaptainsLog(String seed) {
		if (seed.equals("")) {
			//Do a generic captains log
			this.seed = "Captain's log, ";
			chain = new KeyPhrase(this.seed, true).chain;
			defaultMode = true;
		} else {
			this.seed = seed;
			chain = new MarkovChain();
			SeriesReader seriesReader = new SeriesReader(Series.TOS);
			while (seriesReader.hasNextSeries()) {
				ChainBuilder.THIS.setLabel("Reading series " + seriesReader.getSeries().toString());
				for (String cur : seriesReader) {
					if (!cur.equals(""))
						chain.addWords(cur + "#");
				}
				seriesReader.nextSeries();
			}
			defaultMode = false;
		}
	}

	public String generate() {
		String log;
		if (defaultMode)
			log = chain.generateSentence();
		else {
			log = "Captain's log, ";
			if (Math.random() <= .4) {
				log += "supplemental. ";
			} else {
				log += "stardate " + getStardate() + ". ";
			}
			log += chain.generateSentenceWithSeed(seed);
		}
		return log;
	}

	/**
	 * Pick a random stardate between 1312 and 56947
	 *
	 * @return String
	 */
	private static String getStardate() {
		Random rand = new Random();
		int p1 = rand.nextInt((56947 - 1312) + 1) + 1312;
		int p2 = rand.nextInt(10);
		return p1 + "." + p2;
	}
}
