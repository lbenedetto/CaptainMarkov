package captainmarkov.generators

import captainmarkov.getters.SeriesReader
import captainmarkov.utils.MarkovChain
import captainmarkov.utils.Series
import java.util.*

class KeyPhrase : Generator {
    //TODO:Add support for regex to make it possible to get all types of captains logs
    //Enterprise uses "Captain's starlog" most of the time, and there are also many other kinds of logs
    //That it would be fun to include.
    private val keyPhrase: String
    private val cutToPhrase: Boolean
    private var series: Series? = null
    var chain: MarkovChain? = null


    /**
     * Constructor for KeyPhrase
     *
     * @param phrase      String
     * @param cutToPhrase boolean
     * @param series      Series
     */
    constructor(phrase: String, cutToPhrase: Boolean, series: Series) {
        keyPhrase = phrase
        this.cutToPhrase = cutToPhrase
        this.series = series
        buildChain()
    }

    /**
     * Constructor for KeyPhrase for all series
     *
     * @param cutToPhrase boolean
     * @param phrase      String
     */
    constructor(phrase: String, cutToPhrase: Boolean) {
        keyPhrase = phrase
        this.cutToPhrase = cutToPhrase

        buildChains(Series.values().toList())
    }

    /**
     * Constructor for KeyPhrase for given series
     *
     * @param shows       ArrayList
     * @param cutToPhrase boolean
     * @param phrase      String
     */
    constructor(phrase: String, cutToPhrase: Boolean, shows: ArrayList<Series>) {
        this.cutToPhrase = cutToPhrase
        this.keyPhrase = phrase
        chain = null
        buildChains(shows)
    }

    private fun buildChains(shows: List<Series>) {
        for (series in shows) {
            if (chain == null) {
                this.series = series
                buildChain()
            } else {
                merge(KeyPhrase(keyPhrase, cutToPhrase, series))
            }
        }
    }

    override fun generate(): String {
        return chain!!.generateSentence()
    }

    fun merge(that: KeyPhrase) {
        this.chain!!.merge(that.chain!!)
    }

    /**
     * Save lines matching this keyword
     */
    fun buildChain() {
        chain = MarkovChain()
        SeriesReader.readAll()
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
                chain!!.addWords("$it#")
            }
    }
}
