package CaptainMarkov.getters;

import CaptainMarkov.Menu;
import CaptainMarkov.utils.Series;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class SeriesReader implements Iterable<String> {
	private Series series;

	/**
	 * Constructor for SeriesReader
	 *
	 * @param _series Series
	 */
	public SeriesReader(Series _series) {
		series = _series;
	}

	public void nextSeries() {
		switch (series) {
			case TOS:
				series = Series.TNG;
				break;
			case TNG:
				series = Series.DS9;
				break;
			case DS9:
				series = Series.VOY;
				break;
			case VOY:
				series = Series.ENT;
				break;
			case ENT:
				series = null;
		}
	}

	public boolean hasNextSeries() {
		return series != null;
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@Override
	public SeriesIterator iterator() {
		SeriesIterator si = new SeriesIterator();
		si.nextEpisode();
		si.nextLine();
		return si;
	}

	private class SeriesIterator implements Iterator<String> {
		String nextLine;
		String currLine;
		BufferedReader currentEpisode;

		/**
		 * Move to the next episode
		 */

		void nextEpisode() {
			if (series.hasNextEpisode()) series.moveToNextEpisode();
			try {
				if (Menu.deepLogging) System.out.println("Reading episode " + series.currentEpisodeNum);
				currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.name +
						"/Episode " + series.currentEpisodeNum + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("Could not find script file for episode " + series.currentEpisodeNum + ", downloading now");
				ScriptScraper.downloadEpisode(series, series.currentEpisodeNum);
				try {
					currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.name +
							"/Episode " + series.currentEpisodeNum + ".txt"));
				} catch (FileNotFoundException ex) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Move to the next line
		 */
		void nextLine() {
			try {
				nextLine = currentEpisode.readLine();
				while (nextLine.equals("#"))
					nextLine = currentEpisode.readLine();
			} catch (IOException | NullPointerException e) {
				nextLine = null;
			}
		}

		@Override
		public boolean hasNext() {
			return nextLine != null;
		}

		/**
		 * Returns the next element in the iteration.
		 *
		 * @return the next element in the iteration
		 */
		@Override
		public String next() {
			currLine = nextLine;
			nextLine();
			if (nextLine == null && series.hasNextEpisode()) {
				nextEpisode();
				nextLine();
			}//if nextLine remains null, hasNextLine will return false, so there is nothing to worry about
			return currLine;
		}
	}
}
