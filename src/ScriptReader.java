import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScriptReader {
	int currentEpisodeNum;
	BufferedReader currentEpisode;

	public ScriptReader() {
		currentEpisodeNum = 100;
		nextEpisode();
	}

	public void nextEpisode() {
		if (currentEpisodeNum == 101) currentEpisodeNum++;
		currentEpisodeNum++;
		try {
			currentEpisode = new BufferedReader(new FileReader("Episode " + currentEpisodeNum + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String nextLog() {
		String line = "";
		boolean recordingLog = false;
		try {
			String curr = currentEpisode.readLine().trim();
			while (hasNext()) {
				try {
					if (curr.contains("Captain's log"))
						recordingLog = true;
					if (recordingLog) {
						if (curr.trim().isEmpty() || curr.startsWith("[") || curr.contains(":")) {
							return line.trim() + "#";
						}
						line += curr + " ";
					}
					curr = currentEpisode.readLine().trim();
				} catch (NullPointerException e) {
					nextEpisode();
					curr = currentEpisode.readLine().trim();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line.trim();
	}

	public boolean hasNext() {
		return currentEpisodeNum < 277;
	}
}
