package captainmarkov.generators

import captainmarkov.getters.SeriesReader
import captainmarkov.gui.ChainBuilder
import captainmarkov.utils.MarkovChain
import captainmarkov.utils.Series
import java.util.*

class CaptainsLog(seed: String) : Generator {
    private val chain: MarkovChain?
    private val seed: String
    private val defaultMode: Boolean

    /**
     * Pick a random stardate between 1312 and 56947
     *
     * @return String
     */
    private val stardate: String
        get() {
            val rand = Random()
            val p1 = rand.nextInt(56947 - 1312 + 1) + 1312
            val p2 = rand.nextInt(10)
            return "$p1.$p2"
        }

    init {
        if (seed == "") {
            //Do a generic captains log
            this.seed = "Captain's log, "
            chain = KeyPhrase(this.seed, true).chain
            defaultMode = true
        } else {
            this.seed = seed
            chain = MarkovChain()

            SeriesReader.readAll()
                .filter { it.isNotBlank() }
                .forEach { chain.addWords("$it#") }

            defaultMode = false
        }
    }

    override fun generate(): String {
        var log: String
        if (defaultMode)
            log = chain!!.generateSentence()
        else {
            log = "Captain's log, "
            log += if (Math.random() <= .4) {
                "supplemental. "
            } else {
                "stardate $stardate. "
            }
            log += chain!!.generateSentenceWithSeed(seed)
        }
        return log
    }
}
