import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PositronicBrain {
	public static HashMap<Series, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in Voyager's numbering, so we'll skip over those
	public static HashMap<Integer, Integer> voyagerEpisodeGaps;

	public static void main(String[] args) {
		initializeSkippedEpisodeNums();
		initializeVoyagerGaps();

		MarkovChain captainsLogs = new MarkovChain(new KeyWord("Captain's log", false, Series.NextGen));
		MarkovChain commands = new MarkovChain(new KeyWord("Computer, ", true, Series.NextGen));
		String[] characterNames = {"Picard", "Data", "Riker", "LaForge", "Troi", "Crusher", "Wesley", "Worf", "Q", "Computer"};
		MarkovChain[] characters = new MarkovChain[characterNames.length];
		//Fill the characters array
		for (int i = 0; i < characterNames.length; i++)
				characters[i] = new MarkovChain(new Character(characterNames[i], Series.NextGen));
		//Generate a captains log
		generate(10, captainsLogs);
		//Generate a command to the computer
		generate(5, commands);
		//Generate a line of dialogue from a random character
		generate(5, characters[ThreadLocalRandom.current().nextInt(0, characterNames.length)]);
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

		voyagerEpisodeGaps.put(120, 200);
		voyagerEpisodeGaps.put(226, 300);
		voyagerEpisodeGaps.put(322, 400);
		voyagerEpisodeGaps.put(424, 500);
		voyagerEpisodeGaps.put(526, 600);
		voyagerEpisodeGaps.put(626, 700);
	}

	public static boolean nextEpisodeNumIsSkipped(int _currentEpisodeNum, Series _series) {
		for (int skippedNum : skippedEpisodeNums.get(_series)) {
			if (skippedNum == _currentEpisodeNum + 1)
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
