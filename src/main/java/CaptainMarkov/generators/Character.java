package CaptainMarkov.generators;

import java.io.*;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.utils.IterableFile;
import CaptainMarkov.utils.Series;
import CaptainMarkov.Menu;

public class Character {
	private final String characterExternalName;
	private final String character;
	private final Series series;
	public IterableFile file;

	/**
	 * Constructor for generators.Character
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
		try {
			PrintWriter txtFile = new PrintWriter(new FileWriter("./characters/" + series.name + "/" + characterExternalName + ".txt", true));
			for (String curr : seriesReader) {
				//Remove parentheses and brackets
				curr = curr.replaceAll("\\(.*?\\)", "");
				curr = curr.replaceAll("\\[.*?\\]", "");
				curr = curr.trim();
				if (curr.startsWith(character)) {
					String out = curr.trim() + "#";
					if (!out.equals("#"))
						txtFile.println(out);
				}
			}
			txtFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
