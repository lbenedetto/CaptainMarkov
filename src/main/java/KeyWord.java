import java.io.*;

public class KeyWord extends LineGetter {
	String keyPhrase;
	boolean cutToPhrase;

	public KeyWord(String phrase, boolean c, Series _series) {
		super(_series);
		keyPhrase = phrase;
		cutToPhrase = c;
		while (lines == null) {
			try {
				lines = new BufferedReader(new FileReader(series.toString() + "/" + keyPhrase + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found, creating file");
				saveLines();
			}
		}
		nextLine();
	}

	public void saveLines() {
		System.out.println("Saving lines with " + keyPhrase);
		boolean recordingLog = false;
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter(series.toString() + "/" + keyPhrase + ".txt", true));
			String curr = currentEpisode.readLine().trim();
			while (hasNextEpisode()) {
				try {
					if (curr.contains(keyPhrase)) {
						if (cutToPhrase) curr = curr.substring(curr.indexOf(keyPhrase));
						recordingLog = true;
					}
					if (recordingLog) {
						if (curr.trim().isEmpty() || curr.startsWith("[") || curr.startsWith("(") || curr.contains(":")) {
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
}
