import java.util.HashMap;

public class PositronicBrain {
	public static HashMap<Series, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in VOY's numbering, so we'll skip over those
	public static HashMap<Integer, Integer> voyagerEpisodeGaps;

	public static HashMap<String, HashMap<String, MarkovChain>> createMarkovChains() {
		initializeSkippedEpisodeNums();
		initializeVoyagerGaps();
		HashMap<String, HashMap<String, MarkovChain>> chains = new HashMap<>();
		chains.put("TOS", createMarkovChainsForSeries(Series.TOS, new String[]{"Kirk", "Spock", "McCoy", "Uhura", "Chekov", "Sulu", "Computer"}));
		createMarkovChainsForSeries(Series.TNG, new String[]{"Picard", "Data", "Riker", "LaForge", "Troi", "Crusher", "Wesley", "Worf", "Q", "Computer"});
		createMarkovChainsForSeries(Series.DS9, new String[]{"Sisko", "O'Brien", "Bashir", "Worf", "Odo", "Kira", "Dax", "Ezri", "Quark", "Dukat", "Garak", "Weyoun", "Founder", "Nog", "Rom", "Computer"});
		createMarkovChainsForSeries(Series.VOY, new String[]{"Janeway", "Paris", "Chakotay", "Torres", "Neelix", "EMH", "Tuvok", "Computer"});
		//For some reason the script refers to him as both Jonathan and Archer
		createMarkovChainsForSeries(Series.ENT, new String[]{"Jonathan", "Archer", "Reed", "Tucker", "Travis", "Hoshi", "T'Pol", "Phlox", "Computer"});
		return chains;
	}

	public static HashMap<String, MarkovChain> createMarkovChainsForSeries(Series series, String[] characterNames) {
		System.out.println("Creating markov chains for " + series.toString());
		//this phrased was used more on DS9 than captain's log since Sisko was a commander at first
		String logPhrase = series == Series.DS9 ? "Station log" : "Captain's log";
		HashMap<String, MarkovChain> chains = new HashMap<>();
		chains.put("Logs", new MarkovChain(new KeyWord(logPhrase, false, series)));
		chains.put("Commands", new MarkovChain(new KeyWord("Computer, ", true, series)));
		//Fill the Characters array
		for (String characterName : characterNames)
			chains.put(characterName, new MarkovChain(new Character(characterName, series)));
		return chains;
	}

	protected static void initializeSkippedEpisodeNums() {
		skippedEpisodeNums = new HashMap<>();
		skippedEpisodeNums.put(Series.TOS, new int[]{});
		skippedEpisodeNums.put(Series.TNG, new int[]{102});
		skippedEpisodeNums.put(Series.DS9, new int[]{402, 474});
		skippedEpisodeNums.put(Series.VOY, new int[]{});
		skippedEpisodeNums.put(Series.ENT, new int[]{2});
	}

	protected static void initializeVoyagerGaps() {
		voyagerEpisodeGaps = new HashMap<>();
		voyagerEpisodeGaps.put(120, 201);
		voyagerEpisodeGaps.put(226, 301);
		voyagerEpisodeGaps.put(322, 401);
		voyagerEpisodeGaps.put(424, 501);
		voyagerEpisodeGaps.put(526, 601);
		voyagerEpisodeGaps.put(626, 701);
	}

	public static boolean episodeNumIsSkipped(int _currentEpisodeNum, Series _series) {
		for (int skippedNum : skippedEpisodeNums.get(_series)) {
			if (skippedNum == _currentEpisodeNum)
				return true;
		}
		return false;
	}

	public static boolean isAtVoyagerGap(int _currentEpisodeNum, Series _series) {
		return _series == Series.VOY && voyagerEpisodeGaps.containsKey(_currentEpisodeNum + 1);
	}

	public static int getNumPastVoyagerGap(int _currentEpisodeNum) {
		return voyagerEpisodeGaps.get(_currentEpisodeNum + 1);
	}
}
