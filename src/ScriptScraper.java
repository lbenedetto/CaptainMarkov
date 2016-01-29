import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ScriptScraper {
	static ArrayList<Integer> specialCases = new ArrayList<>();
	public static void downloadEpisodes() {
		//Comma separated list of the second part of two part episodes
		//This way, those are skipped
		specialCases.add(102);
		for (int i = 101; i < 278; i++) {
			if(specialCases.contains(i)) i++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			System.out.println("Downloading episode number " + i);
			scrapeLink("http://www.chakoteya.net/NextGen/" + i + ".htm", i);
		}
	}

	public static void scrapeLink(String episodeURL, int episodeNum) {
		String episodeText = "Episode Number: " + episodeNum + "\n";
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL(episodeURL);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				episodeText += line + "\n";
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
		episodeText = episodeText.replaceAll("</div>(.*\n)*.*", "");
		episodeText = episodeText.replaceAll("<[^>]*>", "");
		episodeText = episodeText.replace("&nbsp", "");

		saveEpisode(episodeText, episodeNum);
	}

	public static void saveEpisode(String s, int n) {
		PrintWriter txtFile = null;
		try {
			txtFile = new PrintWriter(new FileWriter("Episode " + n + ".txt", true));
		} catch (IOException e) {
			System.out.println("Something prevented the program from creating the output file");
			System.exit(-1);
		}
		txtFile.println(s);
		txtFile.close();
	}


}
