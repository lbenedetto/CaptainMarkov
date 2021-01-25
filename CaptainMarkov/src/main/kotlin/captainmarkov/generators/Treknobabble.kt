package captainmarkov.generators

import captainmarkov.getters.SeriesReader
import captainmarkov.gui.ChainBuilder
import captainmarkov.utils.MarkovChain
import captainmarkov.utils.Series
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class Treknobabble(threshold: Double) : Generator {
    var chain: MarkovChain = MarkovChain()
    private var dictionary: HashSet<String>? = null

    init {
        loadDictionary()
        SeriesReader.readAll()
            .map { it.replaceFirst("[A-Z]+:".toRegex(), "") }
            .filter { getCoherencyRatio(it) < threshold }
            .forEach { chain.addWords("$it#") }
    }

    override fun generate(): String {
        return chain.generateSentence()
    }

    /**
     * Load dictionary from file into HashSet
     */
    private fun loadDictionary() {
        dictionary = HashSet()
        val `in` = this.javaClass.getResourceAsStream("resources/dictionary.txt")
        if (`in` == null) {
            legacyLoadDictionary()
            return
        }
        val scanner = Scanner(`in`)
        var s: String? = null
        while (scanner.hasNextLine()) {
            s = scanner.nextLine()
            dictionary!!.add(s)
        }
    }

    /**
     * Load dictionary with old method to support running program from IDE instead of from JAR
     */
    private fun legacyLoadDictionary() {
        try {
            dictionary!!.addAll(
                Files.readAllLines(Paths.get("src/main/java/captainmarkov/generators/resources/dictionary.txt"))
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Uses an English dictionary to rate input sentences for how many fake words they contain.
     * Only accept sentences that are below a (yet to be determined) threshold
     * Possibly 50%, AKA a threshold of 1
     *
     * @param dialogue String
     */
    private fun getCoherencyRatio(dialogue: String): Int {
        var trekWords = 0
        var realWords = 0
        val words = dialogue.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (word in words) {
            if (dictionary!!.contains(word))
                realWords++
            else
                trekWords++
        }
        return if (trekWords == 0) 100 else realWords / trekWords
    }
}
