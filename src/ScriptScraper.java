import java.io.*;
import java.net.URL;

public class ScriptScraper {

	public static void downloadEpisodes() {
		scrapeLink("http://www.chakoteya.net/NextGen/101.htm", 101);
		for (int i = 103; i < 278; i++) {
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
