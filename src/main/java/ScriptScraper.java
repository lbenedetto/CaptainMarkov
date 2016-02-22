import java.io.*;
import java.net.URL;

public class ScriptScraper {
	public static void downloadEpisodes(Series series) {
		int firstEpisodeNumber = 1;

		switch (series) {
			case TNG:
			case VOY:
				firstEpisodeNumber = 101;
				break;
			case DS9:
				firstEpisodeNumber = 401;
		}

		int episodeCuttoff = 0;

		switch (series) {
			case TOS:
				episodeCuttoff = 80;
				break;
			case TNG:
				episodeCuttoff = 278;
				break;
			case DS9:
				episodeCuttoff = 576;
				break;
			case VOY:
				episodeCuttoff = 723;
				break;
			case ENT:
				episodeCuttoff = 99;
		}

		for (int i = firstEpisodeNumber; i < episodeCuttoff; i++) {
			if (PositronicBrain.episodeNumIsSkipped(i, series)) i++;
			else if (PositronicBrain.isAtVoyagerGap(i, series)) i = PositronicBrain.getNumPastVoyagerGap(i);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			System.out.println("Downloading episode number " + i + " from series " + series);

			//ENT's scripts use two digits for the production number, so we need to pad the episode number
			//	with a 0 for the single-digit episodes.
			String episodeNumString = Integer.toString(i);
			if (series == Series.ENT && i < 10)
				episodeNumString = "0" + i;

			scrapeLink("http://www.chakoteya.net/" + series.toString() + "/" + episodeNumString + ".htm", i, series);
		}
	}
	public static void downloadEpisode(Series series, int episode){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Downloading episode number " + episode);
		//ENT's scripts use two digits for the production number, so we need to pad the episode number
		//	with a 0 for the single-digit episodes.
		String episodeNumString = Integer.toString(episode);
		if (series == Series.ENT && episode < 10)
			episodeNumString = "0" + episode;
		scrapeLink("http://www.chakoteya.net/" + series.toString() + "/" + episodeNumString + ".htm", episode, series);
		System.out.println("Downloading episode number " + episode + " from series " + series);
	}

	public static void scrapeLink(String episodeURL, int episodeNum, Series series) {
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
		episodeText = episodeText.replaceAll("\\n\\n\\n\\n", "\n");
		episodeText = episodeText.replaceAll("\\n\\n", "\n");
		//For some reason, Enterprise has a ton of this, and it breaks things down the line
		episodeText = episodeText.replaceAll(":;", ": ");

		saveEpisode(episodeText, episodeNum, series);
	}

	public static void saveEpisode(String s, int n, Series series) {
		PrintWriter txtFile;
		String seriesString = series.toString();
		File dir = new File("./scripts/" + seriesString);


		dir.mkdir();
		try {
			FileReader file = new FileReader("./scripts/" + seriesString +"/Episode " + n + ".txt");
		} catch (FileNotFoundException e) {
			try {
				txtFile = new PrintWriter(new FileWriter("./scripts/" + seriesString +"/Episode " + n + ".txt", true));
				txtFile.println(s);
				txtFile.close();
			} catch (IOException ex) {
				System.out.println("Something prevented the program from creating the output file");
				System.exit(-1);
			}
		}
	}


}
