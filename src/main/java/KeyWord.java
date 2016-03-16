import java.io.*;

class KeyWord extends LineGetter {
	private final String keyPhrase;
	private final boolean cutToPhrase;

	/**
	 * Constructor for KeyWord
	 *
	 * @param phrase      String
	 * @param cutToPhrase boolean
	 * @param _series     Series
	 */
	public KeyWord(String phrase, boolean cutToPhrase, Series _series) {
		super(_series);
		keyPhrase = phrase;
		this.cutToPhrase = cutToPhrase;
		while (lines == null) {
			try {
				if(Menu.deepLogging)System.out.println("Reading file ./keyWords/" + series.name + "/" + keyPhrase + ".txt");
				lines = new BufferedReader(new FileReader("./keyWords/" + series.name + "/" + keyPhrase + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found, creating file ./keyWords/" + series.name + "/" + keyPhrase + ".txt");
				saveLines();
			}
		}
		nextLine();
	}

	/**
	 * Save lines matching this keyword
	 */
	private void saveLines() {
		System.out.println("Saving lines with " + keyPhrase);
		boolean recordingLog = false;
		if (!new File("./keyWords/" + series.name).mkdirs()) {
			System.out.println("Failed to create directory ./keyWords/" + series.name);
		}
		try {
			String line = "";
			PrintWriter txtFile = new PrintWriter(new FileWriter("keyWords/" + series.name + "/" + keyPhrase + ".txt", true));
			String curr = currentEpisode.readLine().trim();
			while (series.hasNextEpisode()) {
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
