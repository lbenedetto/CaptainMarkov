import java.io.*;
import java.net.URL;

class ScriptScraper {
	static void downloadEpisode(Series series, int episode) {
		System.out.println("Downloading episode number " + episode + " from series " + series);
		//ENT's scripts use two digits for the production number, so we need to pad the episode number
		//	with a 0 for the single-digit episodes.
		String episodeNumString = Integer.toString(episode);
		if (series == Series.ENT && episode < 10)
			episodeNumString = "0" + episode;
		scrapeLink("http://www.chakoteya.net/" + series.name + "/" + episodeNumString + ".htm", episode, series);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Scrape the given episode using the provided information
	 *
	 * @param episodeURL String
	 * @param episodeNum int
	 * @param series     Series
	 */
	private static void scrapeLink(String episodeURL, int episodeNum, Series series) {
		String episodeText = "Episode Number: " + episodeNum + "<br>";
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL(episodeURL);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				episodeText += line + " ";
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
		episodeText =  episodeText.replaceAll("\\n", "");
		episodeText = episodeText.replaceAll("<br>", "\n");
		episodeText = episodeText.replaceAll("</div>(.*\n)*.*", "\n");
		episodeText = episodeText.replaceAll("<[^>]*>", "\n");
		episodeText = episodeText.replace("&nbsp", "");
		episodeText = episodeText.replaceAll("\\n+\\s*", "\n");
		episodeText = episodeText.replaceAll("\\n{2,}", "\n");
		//For some reason, Enterprise has a ton of this, and it breaks things down the line
		episodeText = episodeText.replaceAll(":;", ": ");

		saveEpisode(episodeText, episodeNum, series);
	}

	/**
	 * Saves the input string to epNum.txt in the given series folder
	 *
	 * @param text   String
	 * @param epNum  int
	 * @param series Series
	 */
	private static void saveEpisode(String text, int epNum, Series series) {
		PrintWriter txtFile;
		File f = new File("./scripts/" + series.name);
		if (!f.exists())
			if (!f.mkdirs())
				System.out.println("Failed to create directory ./scripts/" + series.name);
		try {
			new FileReader("./scripts/" + series.name + "/Episode " + epNum + ".txt");
		} catch (FileNotFoundException e) {
			try {
				txtFile = new PrintWriter(new FileWriter("./scripts/" + series.name + "/Episode " + epNum + ".txt", true));
				txtFile.println(text);
				txtFile.close();
			} catch (IOException ex) {
				System.out.println("Something prevented the program from creating the output file");
				System.exit(-1);
			}
		}
	}


}
