package CaptainMarkov.generators;

import CaptainMarkov.getters.SeriesReader;
import CaptainMarkov.gui.ChainBuilder;
import CaptainMarkov.utils.MarkovChain;
import CaptainMarkov.utils.Series;

public class Annotation extends Generator {
	public final MarkovChain chain;

	public Annotation() {
		chain = new MarkovChain();
		SeriesReader seriesReader = new SeriesReader(Series.TOS);
		while (seriesReader.hasNextSeries()) {
			ChainBuilder.THIS.setLabel("Reading series " + seriesReader.getSeries().toString());
			for (String cur : seriesReader) {
				if (!cur.equals("") && cur.contains("(")) {
					cur = cur.replaceAll(".*?\\(", "");
					cur = cur.replaceAll("\\).*", "");
					chain.addWords(cur + "#");
				}
			}
			seriesReader.nextSeries();
		}
	}

	public String generate() {
		return "(" + chain.generateSentence() + ")";
	}
}
