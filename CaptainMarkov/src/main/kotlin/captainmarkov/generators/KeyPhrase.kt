package captainmarkov.generators

import captainmarkov.getters.SeriesReader
import captainmarkov.utils.MarkovChain
import captainmarkov.utils.Series
import java.util.*

class KeyPhrase(
    private val keyPhrase: String,
    private val cutToPhrase: Boolean,
    series: Array<Series> = Series.values()
) : Generator {
    //TODO:Add support for regex to make it possible to get all types of captains logs
    //Enterprise uses "Captain's starlog" most of the time, and there are also many other kinds of logs
    //That it would be fun to include.
    var chain = MarkovChain()

    init {
        SeriesReader.readAll(series)
            .asSequence()
            .map { it.replace("\\(.*?\\)".toRegex(), "") }
            .map { it.replace("\\[.*?]".toRegex(), "") }
            .filter { it.contains(keyPhrase) }
            .map { if (cutToPhrase) it.substring(it.indexOf(keyPhrase)) else it }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()
            .forEach {
                println(it)
                chain.addWords("$it#")
            }
    }

    override fun generate(): String {
        return chain.generateSentence()
    }
}
