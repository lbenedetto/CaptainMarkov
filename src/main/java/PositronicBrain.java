import java.util.HashMap;

class PositronicBrain {
	private static HashMap<Series, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in VOY's numbering, so we'll skip over those
	private static HashMap<Integer, Integer> voyagerEpisodeGaps;

	/**
	 * Create a HashMap of series names to their Markov Chains
	 * HashMap<String, HashMap<String, MarkovChain>>
	 * HashMap<Series, HashMap<DialogueType, MarkovChain>>
	 *
	 * @return HashMap
	 */
	public static HashMap<String, HashMap<String, MarkovChain>> createMarkovChains() {
		initializeSkippedEpisodeNums();
		initializeVoyagerGaps();
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
	public static HashMap<String, MarkovChain> mixSeries() {
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
		chains.put("Command", new MarkovChain(new KeyWord("Computer, ", true, series)));
		//Fill the Characters array
		for (String characterName : characterNames)
			chains.put(characterName, new MarkovChain(new Character(characterName, series)));
		return chains;
	}

	/**
	 * Initialize the HashMap of skipped episodes for each series
	 */
	private static void initializeSkippedEpisodeNums() {
		skippedEpisodeNums = new HashMap<>();
		skippedEpisodeNums.put(Series.TOS, new int[]{});
		skippedEpisodeNums.put(Series.TNG, new int[]{102});
		skippedEpisodeNums.put(Series.DS9, new int[]{402, 474});
		skippedEpisodeNums.put(Series.VOY, new int[]{});
		skippedEpisodeNums.put(Series.ENT, new int[]{2});
	}

	/**
	 * Initialize the gaps in Voyagers production numbers
	 */
	private static void initializeVoyagerGaps() {
		voyagerEpisodeGaps = new HashMap<>();
		voyagerEpisodeGaps.put(120, 201);
		voyagerEpisodeGaps.put(226, 301);
		voyagerEpisodeGaps.put(322, 401);
		voyagerEpisodeGaps.put(424, 501);
		voyagerEpisodeGaps.put(526, 601);
		voyagerEpisodeGaps.put(626, 701);
	}

	/**
	 * Check if the given episode num is skipped by the given series
	 *
	 * @param _currentEpisodeNum int
	 * @param _series            Series
	 * @return boolean
	 */
	public static boolean episodeNumIsSkipped(int _currentEpisodeNum, Series _series) {
		for (int skippedNum : skippedEpisodeNums.get(_series)) {
			if (skippedNum == _currentEpisodeNum)
				return true;
		}
		return false;
	}

	/**
	 * Check if the current series is Voyager and if the current episode is in a gap
	 *
	 * @param _currentEpisodeNum int
	 * @param _series            Series
	 * @return boolean
	 */
	public static boolean isAtVoyagerGap(int _currentEpisodeNum, Series _series) {
		return _series == Series.VOY && voyagerEpisodeGaps.containsKey(_currentEpisodeNum + 1);
	}

	/**
	 * Get the next episode number for Voyager
	 *
	 * @param _currentEpisodeNum int
	 * @return int
	 */
	public static int getNumPastVoyagerGap(int _currentEpisodeNum) {
		return voyagerEpisodeGaps.get(_currentEpisodeNum + 1);
	}
}
