import com.sun.applet2.AppletParameters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public abstract class LineGetter {
	BufferedReader currentEpisode;
	int currentEpisodeNum;
	BufferedReader lines;
	String nextLine;
	Series series;

	HashMap<Series, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in Voyager's numbering, so we'll skip over those
	HashMap<Integer, Integer> voyagerEpisodeGaps;

	public LineGetter(Series _series) {
		initializeSkippedEpisodeNums();
		initializeVoyagerGaps();

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

	protected void initializeSkippedEpisodeNums() {
		skippedEpisodeNums = new HashMap<>();

		skippedEpisodeNums.put(Series.StarTrek, new int[]{});
		skippedEpisodeNums.put(Series.NextGen, new int[]{102});
		skippedEpisodeNums.put(Series.DS9, new int[]{402, 474});
		skippedEpisodeNums.put(Series.Voyager, new int[]{});
		skippedEpisodeNums.put(Series.Enterprise, new int[]{2});
	}

	protected void initializeVoyagerGaps() {
		voyagerEpisodeGaps = new HashMap<>();

		voyagerEpisodeGaps.put(120, 200);
		voyagerEpisodeGaps.put(226, 300);
		voyagerEpisodeGaps.put(322, 400);
		voyagerEpisodeGaps.put(424, 500);
		voyagerEpisodeGaps.put(526, 600);
		voyagerEpisodeGaps.put(626, 700);
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

	public boolean nextEpisodeNumIsSkipped(int _currentEpisodeNum, Series _series) {
		for (int skippedNum : skippedEpisodeNums.get(_series)) {
			if (skippedNum == _currentEpisodeNum + 1)
					return true;
		}

		return false;
	}

	public boolean isAtVoyagerGap(int _currentEpisodeNum, Series _series) {
		return _series == Series.Voyager && voyagerEpisodeGaps.containsKey(_currentEpisodeNum + 1);
	}

	public int getNumPastVoyagerGap(int _currentEpisodeNum) {
		return voyagerEpisodeGaps.get(_currentEpisodeNum + 1);
	}

	public void nextEpisode() {
		if (nextEpisodeNumIsSkipped(currentEpisodeNum, series)) currentEpisodeNum++;
		else if (isAtVoyagerGap(currentEpisodeNum, series)) currentEpisodeNum = getNumPastVoyagerGap(currentEpisodeNum);
		currentEpisodeNum++;
		try {
			String currentEpisodeNumString = currentEpisodeNum;

			//Enterprise's scripts use two digits for the production number, so we need to pad the episode number
			//	with a 0 for the single-digit episodes.
			if (series == Series.Enterprise && currentEpisodeNum < 10)
				currentEpisodeNumString = "0" + currentEpisodeNum;

			currentEpisode = new BufferedReader(new FileReader("./scripts/" + series.toString() +
					"/Episode " + currentEpisodeNumString + ".txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find script file, downloading now");
			ScriptScraper.downloadEpisodes();
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
