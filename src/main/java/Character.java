import java.io.*;

class Character {
	private String characterExternalName;
	private String character;
	private Series series;
	IterableFile file;

	/**
	 * Constructor for Character
	 *
	 * @param cName  String
	 * @param series Series
	 */
	public Character(String cName, Series series) {
		this.series = series;
		characterExternalName = cName;
		character = cName.toUpperCase() + ":";
		while (file == null) {
			try {
				if (Menu.deepLogging)
					System.out.println("Reading file ./characters/" + series.name + "/" + characterExternalName + ".txt");
				file = new IterableFile("./characters/" + series.name + "/" + characterExternalName + ".txt");
			} catch (FileNotFoundException e) {
				if (Menu.deepLogging)
					System.out.println("File not found, creating file ./characters/" + series.name + "/" + characterExternalName + ".txt");
				saveLines();
			}
		}
	}

	/**
	 * Save lines spoken by this character to a txt file
	 */
	private void saveLines() {
		SeriesReader seriesReader = new SeriesReader(series);
		//Create the directory
		File f = new File("./characters/" + series.name);
		if (!f.exists())
			if (!f.mkdirs())
				System.out.println("Failed to create directory ./characters/" + series.name);
		System.out.println("Saving lines spoken by " + characterExternalName);
		boolean recordingLine = false;
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("./characters/" + series.name + "/" + characterExternalName + ".txt", true));
			for (String curr : seriesReader) {
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
			}
			txtFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
