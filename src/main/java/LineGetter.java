import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class LineGetter {
	BufferedReader currentEpisode;
	int currentEpisodeNum;
	BufferedReader lines;
	String nextLine;
	Series series;

	public LineGetter(Series _series) {
		series = _series;

		switch (series) {
			case TNG:
			case VOY:
				currentEpisodeNum = 100;
				break;
			case DS9:
				currentEpisodeNum = 400;
				break;
			case TOS:
			case ENT:
			default:
				currentEpisodeNum = 0;
		}

		nextEpisode();
	}

	public boolean hasNextEpisode() {
		switch (series) {
			case TOS:
				return currentEpisodeNum < 79;
			case TNG:
				return currentEpisodeNum < 277;
			case DS9:
				return currentEpisodeNum < 575;
			case VOY:
				return currentEpisodeNum < 272;
			case ENT:
				return currentEpisodeNum < 98;
			default:
				return false;
		}
	}

	public void nextEpisode() {
		if (PositronicBrain.episodeNumIsSkipped(currentEpisodeNum + 1, series))
			currentEpisodeNum++;
		else if (PositronicBrain.isAtVoyagerGap(currentEpisodeNum + 1, series))
			currentEpisodeNum = PositronicBrain.getNumPastVoyagerGap(currentEpisodeNum + 1);

		currentEpisodeNum++;
		try {

			currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.toString() +
					"/Episode " + currentEpisodeNum + ".txt"));
		} catch (FileNotFoundException e) {
			//TODO: Do this per episode instead of per series
			System.out.println("Could not find script file, downloading now");
			ScriptScraper.downloadEpisodes(series);
		}
	}

	public void nextLine() {
		try {
			nextLine = lines.readLine();
			while (nextLine.equals("#"))
				nextLine = lines.readLine();
		} catch (IOException | NullPointerException e) {
			nextLine = null;
		}

	}

	public String getNextLine() {
		String out = nextLine;
		nextLine();
		return out;
	}

	public boolean hasNextLine() {
		return nextLine != null;
	}
}
