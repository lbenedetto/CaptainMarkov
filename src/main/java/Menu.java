import java.util.HashMap;
import java.util.Scanner;

public class Menu {
	public static void main(String[] args) {
		HashMap<String, HashMap<String, MarkovChain>> chains = PositronicBrain.createMarkovChains();
		HashMap<String, MarkovChain> seriesChain;
		String series = "";
		String choice;
		int n;
		MarkovChain curr;
		while (!series.equals("exit")) {
			System.out.println("Enter Series Name");
			System.out.println("TOS, TNG, DS9, VOY, ENT");
			series = readString();
			seriesChain = chains.get(series);
			System.out.println("Enter type of line to generate");
			System.out.println("Log, Command, or character name");
			choice = readString();
			curr = seriesChain.get(choice);
			System.out.println("How many do you want to generate at a time");
			n = readInt();
			while (true) {
				curr.generateSentences(n);
				System.out.println("Type back to go back");
				if (readString().equals("back")) break;
			}
			System.out.println("Type exit to exit, or select a new series");
			series = readString();
		}
	}

	private static String readString() {
		Scanner kb = new Scanner(System.in);
		while (!kb.hasNextLine()) {
			kb.next();
		}
		return kb.nextLine();
	}

	private static int readInt() {
		Scanner kb = new Scanner(System.in);
		int out = 0;
		while (out <= 0) {
			while (!kb.hasNextInt())
				kb.next();
			out = kb.nextInt();
			if (out <= 0) System.out.println("Must be positive");
		}
		return out;
	}
}
