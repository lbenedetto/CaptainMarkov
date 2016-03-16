import java.util.HashMap;

class PositronicBrain {
	/**
	 * Create a HashMap of series names to their Markov Chains
	 * HashMap<String, HashMap<String, MarkovChain>>
	 * HashMap<Series, HashMap<DialogueType, MarkovChain>>
	 *
	 * @return HashMap
	 */
	public static HashMap<String, HashMap<String, MarkovChain>> createMarkovChains() {
		HashMap<String, HashMap<String, MarkovChain>> chains = new HashMap<>();
		chains.put("TOS", createMarkovChainsForSeries(Series.TOS, new String[]{"Kirk", "Spock", "McCoy", "Uhura", "Chekov", "Sulu", "Computer"}));
		chains.put("TNG", createMarkovChainsForSeries(Series.TNG, new String[]{"Picard", "Data", "Riker", "LaForge", "Troi", "Crusher", "Wesley", "Worf", "Q", "Computer"}));
		chains.put("DS9", createMarkovChainsForSeries(Series.DS9, new String[]{"Sisko", "O'Brien", "Bashir", "Worf", "Odo", "Kira", "Dax", "Ezri", "Quark", "Dukat", "Garak", "Weyoun", "Founder", "Nog", "Rom", "Computer"}));
		chains.put("VOY", createMarkovChainsForSeries(Series.VOY, new String[]{"Janeway", "Paris", "Chakotay", "Torres", "Neelix", "EMH", "Tuvok", "Computer"}));
		//For some reason the script refers to him as both Jonathan and Archer
		chains.put("ENT", createMarkovChainsForSeries(Series.ENT, new String[]{"Jonathan", "Archer", "Reed", "Tucker", "Travis", "Hoshi", "T'Pol", "Phlox", "Computer"}));
		chains.put("ALL", mixSeries());
		return chains;
	}

	/**
	 * Creates Markov Chains for KeyWords for all series mixed together and stores them in a HashMap
	 *
	 * @return HashMap
	 */
	private static HashMap<String, MarkovChain> mixSeries() {
		HashMap<String, MarkovChain> out = new HashMap<>();
		MarkovChain log = new MarkovChain();
		MarkovChain command = new MarkovChain();
		for (Series series : Series.values()) {
			String logPhrase = series == Series.DS9 ? "Station log" : "Captain's log";
			log.addMoreLines(new KeyWord(logPhrase, false, series));
			command.addMoreLines(new KeyWord("Computer, ", true, series));
		}
		out.put("Log", log);
		out.put("Command", command);
		return out;
	}

	/**
	 * Create a Markov Chain for the specified series
	 * HashMap will contain Markov Chains for all the characters in the String[]
	 * as well as a Markov Chain for logs and commands to the computer
	 *
	 * @param series         Series
	 * @param characterNames String[]
	 * @return HashMap
	 */
	private static HashMap<String, MarkovChain> createMarkovChainsForSeries(Series series, String[] characterNames) {
		System.out.println("Creating markov chains for " + series.toString());
		//this phrased was used more on DS9 than captain's log since Sisko was a commander at first
		String logPhrase = series == Series.DS9 ? "Station log" : "Captain's log";
		HashMap<String, MarkovChain> chains = new HashMap<>();
		chains.put("Log", new MarkovChain(new KeyWord(logPhrase, false, series)));
		series.resetInstance();
		chains.put("Command", new MarkovChain(new KeyWord("Computer, ", true, series)));
		//Fill the Characters array
		for (String characterName : characterNames) {
			series.resetInstance();
			chains.put(characterName, new MarkovChain(new Character(characterName, series)));
		}
		return chains;
	}
}
