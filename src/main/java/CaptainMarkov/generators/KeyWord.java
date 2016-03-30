package CaptainMarkov.generators;

import java.io.*;

import CaptainMarkov.Menu;
import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.utils.IterableFile;
import CaptainMarkov.utils.Series;

public class KeyWord {
	private final String keyPhrase;
	private final boolean cutToPhrase;
	private final Series series;
	public IterableFile file;

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
				if (Menu.deepLogging)
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
		File f = new File("./keyWords/" + series.name);
		if (!f.exists())
			if (!f.mkdirs())
				System.out.println("Failed to create directory ./keyWords/" + series.name);
		try {
			PrintWriter txtFile = new PrintWriter(new FileWriter("keyWords/" + series.name + "/" + keyPhrase + ".txt", true));
			for (String curr : seriesReader) {
				//Remove parentheses and brackets
				curr = curr.replaceAll("\\(.*?\\)", "");
				curr = curr.replaceAll("\\[.*?\\]", "");
				curr = curr.trim();
				if (curr.contains(keyPhrase)) {
					if (cutToPhrase) curr = curr.substring(curr.indexOf(keyPhrase));
					String out = curr.trim() + "#";
					if (!out.equals("#"))
						txtFile.println(out.trim());
				}
			}
			txtFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
