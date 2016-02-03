import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class LineGetter {
	BufferedReader currentEpisode;
	int currentEpisodeNum;
	BufferedReader lines;
	String nextLine;
	public LineGetter() {
		currentEpisodeNum = 100;
		nextEpisode();
	}

	public boolean hasNextEpisode() {
		return currentEpisodeNum < 277;
	}

	public void nextEpisode() {
		if (currentEpisodeNum == 101) currentEpisodeNum++;
		currentEpisodeNum++;
		try {
			currentEpisode = new BufferedReader(new FileReader("./scripts/Episode " + currentEpisodeNum + ".txt"));
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
