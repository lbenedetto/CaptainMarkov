package captainmarkov.getters


import captainmarkov.utils.Series
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

object SeriesReader {

    fun readAll(series: Array<out Series> = Series.values()): List<String> {
        return series
            .map { readAllFromSeries(it) }
            .flatten()
    }

    fun readAllFromSeries(series: Series): List<String> {
        System.out.println("Reading ${series.seriesName}")
        return Files.walk(Paths.get("./scripts/${series.seriesName}/"))
            .filter { !it.toFile().isDirectory }
            .map { Files.readAllLines(it) }
            .collect(Collectors.toList())
            .flatten()
    }

}