package CaptainMarkov.generators;

import java.io.*;

import CaptainMarkov.Menu;
import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.utils.IterableFile;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

public class KeyWord extends Generator {
	private final String keyPhrase;
	private final boolean cutToPhrase;
	private final Series series;
	private MarkovChain chain;
	private PrintWriter txtFile;
	public IterableFile file;
	public boolean canGenerate;

	/**
	 * Constructor for KeyWord
	 *
	 * @param phrase      String
	 * @param cutToPhrase boolean
	 * @param series      Series
	 */
	public KeyWord(String phrase, boolean cutToPhrase, Series series) {
		canGenerate = false;
		if (phrase.equals("Captain's log"))
			keyPhrase = series == Series.DS9 ? "Station log" : "Captain's log";
		else
			keyPhrase = phrase;
		//Validate filename
		String filename = keyPhrase.replaceAll("[^a-zA-Z0-9.\\s-]", "").toLowerCase();
		this.cutToPhrase = cutToPhrase;
		this.series = series;
		while (file == null) {
			try {
				if (Menu.deepLogging)
					System.out.println("Reading file ./keyWords/" + series.name + "/" + filename + ".txt");
				file = new IterableFile("./keyWords/" + series.name + "/" + filename + ".txt");
			} catch (FileNotFoundException e) {
				if (Menu.deepLogging)
					System.out.println("File not found, creating file ./keyWords/" + series.name + "/" + filename + ".txt");
				if (makeOutputFile(filename))
					saveLines();
			}
		}

	}
	public void buildChain(){
		chain = new MarkovChain(file);
		canGenerate = true;
	}
	public String generate() {
		if(canGenerate)
			return chain.generateSentence();
		throw new NullPointerException("Tried to use chain before building it");
	}

	private boolean makeOutputFile(String filename) {
		File f = new File("./keyWords/" + series.name);
		if (!f.exists())
			if (!f.mkdirs()) {
				System.out.println("Failed to create directory ./keyWords/" + series.name);
				return false;
			}
		try {
			txtFile = new PrintWriter(new FileWriter("keyWords/" + series.name + "/" + filename + ".txt", true));
		} catch (IOException e) {
			System.out.println("Failed to create file keyWords/" + series.name + "/" + filename + ".txt");
			return false;
		}
		return true;
	}

	public void merge(KeyWord that) {
		this.chain.addFile(that.file);
	}

	/**
	 * Save lines matching this keyword
	 */
	private void saveLines() {
		SeriesReader seriesReader = new SeriesReader(series);
		System.out.println("Getting lines matching " + keyPhrase);
		for (String curr : seriesReader) {
			//Remove parentheses and brackets
			curr = curr.replaceAll("\\(.*?\\)", "");
			curr = curr.replaceAll("\\[.*?\\]", "");
			curr = curr.trim();
			if (curr.contains(keyPhrase)) {
				if (cutToPhrase) curr = curr.substring(curr.indexOf(keyPhrase));
				String out = curr.trim() + "#";
				if (!out.equals("#")) {
					txtFile.println(out.trim());
				}
			}
		}
		txtFile.close();
	}
}
