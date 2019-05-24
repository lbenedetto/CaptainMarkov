package captainmarkov.getters

import captainmarkov.utils.HtmlToPlainText
import captainmarkov.utils.Series
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

internal object ScriptScraper {

    @JvmStatic
    fun main(args: Array<String>) {
        Series.values().forEach {
            downloadEpisodes(it)
        }

    }

    data class EpisodeLink(
        var url: String,
        var name: String
    )

    fun downloadEpisodes(series: Series) {
        val url = "http://www.chakoteya.net/${series.seriesName}"

        // This is known to download two random non-episode urls from Voyager page,
        // but I really only needed to run this once so its not worth fixing it.
        Jsoup.connect("$url/${series.urlSuffix}.htm").get()
            .select("a[href]")
            .map { EpisodeLink("$url/${it.attr("href")}", it.text()) }
            .filter { it.url.matches(".*?\\d.html?".toRegex()) }
            .forEach {
                it.name = cleanName(it.name)
                downloadEpisode(series, it)
                Thread.sleep(1000)
            }
    }

    fun downloadEpisode(series: Series, episode: EpisodeLink) {
        println("Downloading ${episode.name} from $series")
        val document = Jsoup.connect(episode.url).get()

        val episodeTextBuilder = StringBuilder()

        episodeTextBuilder.append(document.title().replace(".*?-".toRegex(), "").trim())

        document
            .select("table")
            .map { HtmlToPlainText().getPlainText(it) }
            .forEach { episodeTextBuilder.append(it) }

        val episodeText = episodeTextBuilder.toString()
            .replace("\n ".toRegex(), "\n")
            .replace("\\n{2,}".toRegex(), "\n")
//            //For some reason, Enterprise has a ton of this, and it breaks things down the line
            .replace(":;".toRegex(), ": ")

        saveEpisode(episodeText, episode.name, series)
    }

    private fun saveEpisode(script: String, name: String, series: Series) {
        val path = "./scripts/${series.seriesName}"
        Files.createDirectories(Paths.get(path))

        File("$path/$name.txt").bufferedWriter().use {
            it.write(script)
        }
    }

    private fun cleanName(name: String): String {
        return name.replace("?", "")
            .replace(":", "")
            .replace("\n", " ")
            .trim()
    }


}
