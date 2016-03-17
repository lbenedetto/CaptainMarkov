import java.util.HashMap;

public enum Series {
	TOS("StarTrek", 0),
	TNG("NextGen", 100),
	DS9("DS9", 400),
	VOY("Voyager", 100),
	ENT("Enterprise", 0);
	private final HashMap<String, int[]> skippedEpisodeNums;
	//The script database has a few odd gaps in VOY's numbering, so we'll skip over those
	private final HashMap<Integer, Integer> voyagerEpisodeGaps;
	public final String name;
	public int currentEpisodeNum;
	private boolean canGetNextEpisode;

	Series(String name, int episode) {
		canGetNextEpisode = true;
		this.name = name;
		currentEpisodeNum = episode;
		//Initialize the HashMap of skipped episodes for each series
		skippedEpisodeNums = new HashMap<>();
		skippedEpisodeNums.put("StarTrek", new int[]{});
		skippedEpisodeNums.put("NextGen", new int[]{102});
		skippedEpisodeNums.put("DS9", new int[]{402, 474});
		skippedEpisodeNums.put("Voyager", new int[]{});
		skippedEpisodeNums.put("Enterprise", new int[]{2});
		//Initialize the gaps in Voyagers production numbers
		voyagerEpisodeGaps = new HashMap<>();
		voyagerEpisodeGaps.put(120, 201);
		voyagerEpisodeGaps.put(226, 301);
		voyagerEpisodeGaps.put(322, 401);
		voyagerEpisodeGaps.put(424, 501);
		voyagerEpisodeGaps.put(526, 601);
		voyagerEpisodeGaps.put(626, 701);
	}

	public void resetInstance() {
		switch (this) {
			case TOS:
				currentEpisodeNum = 0;
				break;
			case TNG:
				currentEpisodeNum = 100;
				break;
			case DS9:
				currentEpisodeNum = 400;
				break;
			case VOY:
				currentEpisodeNum = 100;
				break;
			default:
				currentEpisodeNum = 0;
		}
		canGetNextEpisode = true;
	}

	@Override
	public String toString() {
		return name;
	}

	public Series getNextSeries() {
		canGetNextEpisode = true;
		switch (this) {
			case TOS:
				return TNG;
			case TNG:
				return DS9;
			case DS9:
				return VOY;
			case VOY:
				return ENT;
			default://ENT
				return null;
		}
	}

	public boolean hasNextSeries() {
		return !(this == ENT);
	}

	/**
	 * Check if the current episode is the last episode of the series
	 *
	 * @return boolean
	 */
	public boolean hasNextEpisode() {
		canGetNextEpisode = true;
		switch (this) {
			case TOS:
				return currentEpisodeNum < 79;
			case TNG:
				return currentEpisodeNum < 277;
			case DS9:
				return currentEpisodeNum < 575;
			case VOY:
				return currentEpisodeNum < 272;
			case ENT:
				return currentEpisodeNum < 98;
			default:
				return false;
		}
	}

	/**
	 * If the current episode number is skipped, increment the current episode past the gap
	 */
	private void validateEpisodeNum() {
		for (int skippedNum : skippedEpisodeNums.get(this.name)) {
			if (skippedNum == currentEpisodeNum)
				currentEpisodeNum++;
		}
		if (this == VOY && voyagerEpisodeGaps.containsKey(currentEpisodeNum + 1))
			currentEpisodeNum = voyagerEpisodeGaps.get(currentEpisodeNum + 1);
	}

	public void moveToNextEpisode() {
		if (!canGetNextEpisode) throw new IllegalStateException("Tried to get next episode without checking first");
		currentEpisodeNum++;
		validateEpisodeNum();
		canGetNextEpisode = false;
	}
}