import java.io.*;

class Character extends LineGetter {
	private final String characterExternalName;
	private final String character;

	/**
	 * Constructor for Character
	 *
	 * @param cName   String
	 * @param _series Series
	 */
	public Character(String cName, Series _series) {
		super(_series);
		characterExternalName = cName;
		character = cName.toUpperCase() + ":";
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

	/**
	 * Save lines spoken by this character to a txt file
	 */
	private void saveLines() {
		//Create the directory
		String seriesString = series.toString();
		new File("./characters/" + seriesString).mkdir();
		System.out.println("Saving lines spoken by " + characterExternalName);
		boolean recordingLine = false;
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("./characters/" + seriesString + "/" + characterExternalName + ".txt", true));
			String curr = currentEpisode.readLine().trim();
			while (hasNextEpisode()) {
				try {
					if (curr.startsWith(character)) {
						recordingLine = true;
					}
					//Remove parentheses and brackets
					curr = curr.replaceAll("\\(.*?\\)", "");
					curr = curr.replaceAll("\\[.*?\\]", "");
					curr = curr.trim();
					if (curr.equals(character))
						recordingLine = false;
					if (recordingLine) {
						if (curr.trim().isEmpty() || (curr.contains(":") && !curr.startsWith(character))) {
							String out = line.trim() + "#";
							if (!out.equals("#"))
								txtFile.println(line.trim() + "#");
							line = "";
							recordingLine = false;
						}
					}
					if (recordingLine)
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
