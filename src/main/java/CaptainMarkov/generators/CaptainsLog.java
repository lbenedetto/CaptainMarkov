package CaptainMarkov.generators;

import java.util.Calendar;
import java.util.Random;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

public class CaptainsLog extends Generator {
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
	 * Gets the current stardate based on the current date
	 * Based on Star Trek: The Next Generation Writer's/Director's Guide:
	 * <p>
	 * The first two digits of the stardate are always 41.
	 * The 4 stands for 24th century, the 1 indicates first season.
	 * The additional three leading digits will progress unevenly during the course of the season from 000 to 999.
	 * The digit following the decimal point is generally regarded as a day counter.
	 *
	 * @return int
	 */
	private static String getStardateOldMethod() {
		Calendar currentDate = Calendar.getInstance();
		//First digit is century
		String stardate = "1";
		//Add one because months are zero indexed
		int month = currentDate.get(Calendar.MONTH) + 1;
		//83 is largest number you can multiply by 12 and still be less than 1000
		stardate += month == 1 ? "0" + month * 83 : month * 83;
		//4 is smalled number you can divide 31 by to get it to be less than 10
		stardate += "." + currentDate.get(Calendar.DAY_OF_MONTH) / 4;
		return stardate;
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
