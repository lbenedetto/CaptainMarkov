package CaptainMarkov.getters;


import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.IterableFile;
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
	 * @param series Series
	 */
	public SeriesReader(Series series) {
		this.series = series;
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

	public Series getSeries() {
		return series;
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
		si.nextLine = si.currentEpisode.next();
		return si;
	}

	private class SeriesIterator implements Iterator<String> {
		String nextLine;
		String currLine;
		Iterator<String> currentEpisode;

		/**
		 * Move to the next episode
		 */

		void nextEpisode() {
			if (series.hasNextEpisode()) series.moveToNextEpisode();
			try {
				currentEpisode = new IterableFile("./scripts/" + series.name + "/Episode " + series.currentEpisodeNum + ".txt").iterator();
			} catch (FileNotFoundException e) {
				System.out.println("Could not find script file for episode " + series.currentEpisodeNum + ", downloading now");
				ScriptScraper.downloadEpisode(series, series.currentEpisodeNum);
				try {
					currentEpisode = new IterableFile("./scripts/" + series.name + "/Episode " + series.currentEpisodeNum + ".txt").iterator();
				} catch (FileNotFoundException ex) {
					e.printStackTrace();
				}
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
			nextLine = currentEpisode.next();
			if (nextLine == null && series.hasNextEpisode()) {
				nextEpisode();
				nextLine = currentEpisode.next();
			}//if nextLine remains null, hasNext() will return false, so there is nothing to worry about
			ChainBuilder.THIS.setLabel("Reading Episode #" + series.currentEpisodeNum + " in " + series.toString());
			return currLine;
		}
	}
}
