import java.io.*;

public class Character extends LineGetter {
	String characterExternalName;
	String character;

	public Character(String c, Series _series) {
		super(_series);
		characterExternalName = c;
		character = c.toUpperCase() + ":";
		while (lines == null) {
			try {
				lines = new BufferedReader(new FileReader("./characters/" + series.toString() + "/" + characterExternalName + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found, creating file");
				saveLines();
			}
		}
		nextLine();
	}

	public void saveLines() {
		String seriesString = series.toString();
		File dir = new File("./characters/" + seriesString);
		dir.mkdir();
		System.out.println("Saving lines spoken by " + characterExternalName);
		boolean recordingLog = false;
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("./characters/" + seriesString + "/" + characterExternalName + ".txt", true));
			String curr = currentEpisode.readLine().trim();
			while (hasNextEpisode()) {
				try {
					if (curr.startsWith(character)) {
						recordingLog = true;
					}
					curr = curr.replaceAll("\\(.*?\\)", "");
					curr = curr.replaceAll("\\[.*?\\]", "");
					curr = curr.trim();
					if(curr.equals(character))
						recordingLog = false;
					if (recordingLog) {
						if (curr.trim().isEmpty() || (curr.contains(":") && !curr.startsWith(character))) {
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
