package captainmarkov.generators

import captainmarkov.getters.SeriesReader
import captainmarkov.utils.MarkovChain

class Annotation : Generator {
    private val chain: MarkovChain = MarkovChain()

    init {
        SeriesReader.readAll()
            .filter { it.isNotBlank() && it.contains("(") }
            .map { it.replace(".*?\\(".toRegex(), "") }
            .map { it.replace("\\).*".toRegex(), "") }
            .forEach {
                chain.addWords("$it#")
            }
    }

    override fun generate(): String {
        return "(" + chain.generateSentence() + ")"
    }
}
