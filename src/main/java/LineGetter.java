import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

abstract class LineGetter {
	BufferedReader currentEpisode;
	BufferedReader lines;
	private String nextLine;
	Series series;

	/**
	 * Constructor for LineGetter
	 *
	 * @param _series Series
	 */
	LineGetter(Series _series) {
		series = _series;
		nextEpisode();
	}


	/**
	 * Move to the next episode
	 */

	void nextEpisode() {
		if (series.hasNextEpisode()) series.moveToNextEpisode();
		try {
			if(Menu.deepLogging)System.out.println("Reading episode " + series.currentEpisodeNum);
			currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.name +
					"/Episode " + series.currentEpisodeNum + ".txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find script file for episode " + series.currentEpisodeNum + ", downloading now");
			ScriptScraper.downloadEpisode(series, series.currentEpisodeNum);
			try {
				currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.name +
						"/Episode " + series.currentEpisodeNum + ".txt"));
			} catch (FileNotFoundException ex) {
				System.out.println("Somethings fucky");
			}
		}
	}

	/**
	 * Move to the next line
	 */
	void nextLine() {
		try {
			nextLine = lines.readLine();
			while (nextLine.equals("#"))
				nextLine = lines.readLine();
		} catch (IOException | NullPointerException e) {
			nextLine = null;
		}
	}

	/**
	 * Get the next line
	 *
	 * @return String
	 */
	public String getNextLine() {
		String out = nextLine;
		nextLine();
		if (nextLine == null && series.hasNextEpisode()) {
			nextEpisode();
			nextLine();
		}//else nextLine remains null, and hasNextLine will return false.
		return out;
	}

	/**
	 * Check if this has a next line
	 *
	 * @return boolean
	 */
	public boolean hasNextLine() {
		return nextLine != null;
	}
}
