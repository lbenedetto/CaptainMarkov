import java.io.*;

public class ScriptReader {
	int currentEpisodeNum;
	BufferedReader currentEpisode;
	BufferedReader logs;
	String nextLog;

	public ScriptReader() {
		currentEpisodeNum = 100;
		nextEpisode();
		try {
			logs = new BufferedReader(new FileReader("Captains Logs.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("File not found, creating file");
			saveLogs();
		}
		nextLog();
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

	public void saveLogs() {
		System.out.println("Saving log files");
		boolean recordingLog = false;
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("Captains Logs.txt", true));
			String curr = currentEpisode.readLine().trim();
			while (hasNextEpisode()) {
				try {
					if (curr.contains("Captain's log"))
						recordingLog = true;
					if (recordingLog) {
						if (curr.trim().isEmpty() || curr.startsWith("[") || curr.contains(":")) {
							String out = line.trim() + "#";
							if (!out.equals("#"))
								txtFile.println(line.trim() + "#");
							line = "";
							recordingLog = false;
						}
					}
					if (recordingLog)
						line += curr + " ";
					curr = currentEpisode.readLine().trim();
				} catch (NullPointerException e) {
					nextEpisode();
					curr = currentEpisode.readLine().trim();
				}
			}
			txtFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void nextLog() {
		try {
			nextLog = logs.readLine();
			while (nextLog.equals("#"))
				nextLog = logs.readLine();
		} catch (IOException | NullPointerException e) {
			nextLog = null;
		}

	}

	public String getNextLog() {
		String out = nextLog;
		nextLog();
		return out;
	}

	public boolean hasNextLog() {
		return nextLog != null;
	}

	public boolean hasNextEpisode() {
		return currentEpisodeNum < 277;
	}
}
