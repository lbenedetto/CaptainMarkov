import java.util.concurrent.ThreadLocalRandom;

public class PositronicBrain {
	public static void main(String[] args) {
		MarkovChain captainsLogs = new MarkovChain(new KeyWord("Captain's log", false));
		MarkovChain commands = new MarkovChain(new KeyWord("Computer, ", true));
		MarkovChain[] characters = new MarkovChain[8];
		String[] characterNames = {"Picard", "Data", "Riker", "LaForge", "Troi", "Crusher", "Wesley", "Worf"};
		//Fill the characters array
		for (int i = 0; i < characterNames.length; i++)
			characters[i] = new MarkovChain(new Character(characterNames[i]));
		//Generate a captains log
		generate(10, captainsLogs);
		//Generate a command to the computer
		generate(5, commands);
		//Generate a line of dialogue from a random character
		generate(5, characters[ThreadLocalRandom.current().nextInt(0, characterNames.length)]);
	}

	public static void generate(int n, MarkovChain mc){
		for(int i =0; i < n; i++){
			mc.generateSentence();
		}
	}
}
