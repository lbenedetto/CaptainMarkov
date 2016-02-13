import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PositronicBrain {
	public static HashMap<Series, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in Voyager's numbering, so we'll skip over those
	public static HashMap<Integer, Integer> voyagerEpisodeGaps;

	public static void main(String[] args) {
		initializeSkippedEpisodeNums();
		initializeVoyagerGaps();		
		
		createMarkovs(Series.StarTrek, new String[]{"Kirk", "Spock", "McCoy", "Uhura", "Chekov", "Sulu", "Computer"});
		createMarkovs(Series.NextGen, new String[]{"Picard", "Data", "Riker", "LaForge", "Troi", "Crusher", "Wesley", "Worf", "Q", "Computer"});
		createMarkovs(Series.DS9, new String[]{"Sisko", "O'Brien", "Bashir", "Worf", "Odo", "Kira", "Dax", "Ezri", "Quark", "Dukat", "Garak", "Weyoun", "Founder", "Nog", "Rom", "Computer"});
		createMarkovs(Series.Voyager, new String[]{"Janeway", "Paris", "Chakotay", "Torres", "Neelix", "EMH", "Tuvok", "Seven", "Computer"});
		//createMarkovs(Series.Enterprise, new String[]{"Jonathan", "Tucker", "T'Pol", "Phlox", "Computer"});
		// I might have missed some Enterprise characters, I don't know that show very well
	}

	public static void createMarkovs(Series series, String[] characterNames)
	{
		MarkovChain CaptainsLogs = new MarkovChain(new KeyWord("Captain's log", false, series));
		MarkovChain Commands = new MarkovChain(new KeyWord("Computer, ", true, series));
		MarkovChain[] Characters = new MarkovChain[characterNames.length];

		//Fill the Characters array
		for (int i = 0; i < characterNames.length; i++)
			Characters[i] = new MarkovChain(new Character(characterNames[i], series));

		System.out.println("Creating markov chains for " + series.toString());

		//Generate a captains log
		generate(10, CaptainsLogs);
		//Generate a command to the computer
		generate(5, Commands);
		//Generate a line of dialogue from a random character
		generate(5, Characters[ThreadLocalRandom.current().nextInt(0, characterNames.length)]);

	}

	public static void generate(int n, MarkovChain mc) {
		for (int i = 0; i < n; i++) {
			mc.generateSentence();
		}
	}

	protected static void initializeSkippedEpisodeNums() {
		skippedEpisodeNums = new HashMap<>();

		skippedEpisodeNums.put(Series.StarTrek, new int[]{});
		skippedEpisodeNums.put(Series.NextGen, new int[]{102});
		skippedEpisodeNums.put(Series.DS9, new int[]{402, 474});
		skippedEpisodeNums.put(Series.Voyager, new int[]{});
		skippedEpisodeNums.put(Series.Enterprise, new int[]{2});
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
		return _series == Series.Voyager && voyagerEpisodeGaps.containsKey(_currentEpisodeNum + 1);
	}

	public static int getNumPastVoyagerGap(int _currentEpisodeNum) {
		return voyagerEpisodeGaps.get(_currentEpisodeNum + 1);
	}
}
