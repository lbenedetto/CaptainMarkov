import java.io.*;

class KeyWord {
	private final String keyPhrase;
	private final boolean cutToPhrase;
	Series series;
	IterableFile file;

	/**
	 * Constructor for KeyWord
	 *
	 * @param phrase      String
	 * @param cutToPhrase boolean
	 * @param series      Series
	 */
	public KeyWord(String phrase, boolean cutToPhrase, Series series) {
		keyPhrase = phrase;
		this.cutToPhrase = cutToPhrase;
		this.series = series;
		while (file == null) {
			try {
				if (Menu.deepLogging)
					System.out.println("Reading file ./keyWords/" + series.name + "/" + keyPhrase + ".txt");
				file = new IterableFile("./keyWords/" + series.name + "/" + keyPhrase + ".txt");
			} catch (FileNotFoundException e) {
				System.out.println("File not found, creating file ./keyWords/" + series.name + "/" + keyPhrase + ".txt");
				saveLines();
			}
		}
	}

	/**
	 * Save lines matching this keyword
	 */
	private void saveLines() {
		SeriesReader seriesReader = new SeriesReader(series);
		System.out.println("Saving lines with " + keyPhrase);
		boolean recordingLog = false;
		if (!new File("./keyWords/" + series.name).mkdirs()) {
			System.out.println("Failed to create directory ./keyWords/" + series.name);
		}
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("keyWords/" + series.name + "/" + keyPhrase + ".txt", true));
			for (String curr : seriesReader) {
				if (curr.contains(keyPhrase)) {
					if (cutToPhrase) curr = curr.substring(curr.indexOf(keyPhrase));
					recordingLog = true;
				}
				if (recordingLog)
					if (curr.trim().isEmpty() || curr.startsWith("[") || curr.startsWith("(") || curr.contains(":")) {
						String out = line.trim() + "#";
						if (!out.equals("#"))
							txtFile.println(line.trim() + "#");
						line = "";
						recordingLog = false;
					}
				if (recordingLog)
					line += curr + " ";
			}
			txtFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
