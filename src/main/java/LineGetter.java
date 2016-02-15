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
			case NextGen:
			case Voyager:
				currentEpisodeNum = 100;
				break;
			case DS9:
				currentEpisodeNum = 400;
				break;
			case StarTrek:
			case Enterprise:
			default:
				currentEpisodeNum = 0;
		}

		nextEpisode();
	}

	public boolean hasNextEpisode() {
		switch (series) {
			case StarTrek:
				return currentEpisodeNum < 79;
			case NextGen:
				return currentEpisodeNum < 277;
			case DS9:
				return currentEpisodeNum < 575;
			case Voyager:
				return currentEpisodeNum < 272;
			case Enterprise:
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
