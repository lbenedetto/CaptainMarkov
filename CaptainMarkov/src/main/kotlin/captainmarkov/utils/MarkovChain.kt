package captainmarkov.utils

import captainmarkov.gui.ChainBuilder

import java.util.*

class MarkovChain {
    // Hashmap
    private var chain = Hashtable<String, Vector<String>>()
    private val rand = Random()

    init {
        createStarterEntries()
    }

    private fun createStarterEntries() {
        // Create the first two entries (k:_start, k:_end)
        chain["_start"] = Vector()
        chain["_end"] = Vector()
    }

    private fun addLines(lines: List<String>) {
        for (s in lines)
            addWords(s)
    }

    /**
     * input: I wrote useful documentation
     * output: [I wrote, wrote useful, useful documentation]
     *
     * This gives us a chain length of 3 words,
     * which results in a higher coherency.
     */
    private fun getChainLinks(phrase: String): Array<String> {
        return phrase.split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toList()
            .zipWithNext() // Returns a list of pairs of each two adjacent elements in this collection.
            .map { pair -> "${pair.first} ${pair.second}" }
            .toTypedArray()
    }

    /**
     * Removes every other word in the phrase,
     * undoing the duplication of words caused by getChainLinks()
     *
     * @param phrase String
     * @return String
     */
    private fun removeDuplicateWords(phrase: String): String {
        val words = phrase.trim().split(" ")

        val out = words.withIndex()
            .filter { it.index % 2 == 0 }
            .joinToString(separator = " ") { it.value }

        return if (words.size % 2 == 0) out + " " + words[words.size - 1] else out

    }

    /**
     * Add words from phrase to chain
     *
     * @param phrase String
     */

    fun addWords(phrase: String) {
        if (phrase == "#" || phrase == "") return
        // put each word into an array
        val words = getChainLinks(phrase)
        //No point in adding 2 word phrases
        if (words.size < 2) return
        // Loop through each word, check if it's already added
        // if its added, then get the suffix vector and add the word
        // if it hasn't been added then add the word to the list
        // if its the first or last word then select the _start / _end key
        for (i in words.indices) {
            if (i == 0) {
                //Add the first part of the phrase as a suffix for _start
                val startWords = chain["_start"]!!
                startWords.add(words[i])
                var suffix: Vector<String>? = chain[words[i]]
                if (suffix == null) {
                    suffix = Vector()
                    suffix.add(words[i + 1])
                    chain[words[i]] = suffix
                }
            } else if (i == words.size - 1) {
                //Add the last part of the phrase as a suffix for _end
                val endWords = chain["_end"]!!
                endWords.add(words[i])
            } else {
                //Add the current part of the phrase to the appropriate list of suffixes
                var suffix: Vector<String>? = chain[words[i]]
                if (suffix == null) {
                    suffix = Vector()
                    suffix.add(words[i + 1])
                    chain[words[i]] = suffix
                } else {
                    suffix.add(words[i + 1])
                    chain[words[i]] = suffix
                }
            }
        }
    }

    /**
     * Generate and show a sentence from the Markov Chain
     */
    fun generateSentence(): String {
        // String for the next word
        var nextWord = ""
        // Select the first word
        val startWords = chain["_start"]!!
        val startWordsLen = startWords.size
        if (startWordsLen == 0) {
            val error = "The most likely cause is that the phrases you tried to use never occurred in Star Trek"
            println(error)
            ChainBuilder.instance.setLabel(error)
        }
        while (nextWord.isEmpty()) {
            nextWord = startWords[rand.nextInt(startWordsLen)]
        }
        return generateSentenceWithSeed(nextWord)
    }

    fun generateSentenceWithSeed(seed: String): String {

        // Vector to hold the phrase
        val newPhrase = Vector<String>()
        newPhrase.add(seed)
        var nextWord = seed
        // Keep looping through the words until we've reached the end
        while (nextWord[nextWord.length - 1] != '#') {
            val wordSelection = chain[nextWord]
            var wordCandidate: String? = null
            if (wordSelection == null) println("Couldn't find seed in chain")
            var attempts = 0
            while (wordCandidate == null && attempts <= 10) {
                val ix = rand.nextInt(wordSelection!!.size)
                wordCandidate = wordSelection[ix]
                attempts++
            }
            if (wordCandidate == null) {
                println("Couldn't find next word candidate")
                break
            }
            if (wordCandidate.isNotEmpty()) {
                nextWord = wordCandidate
                newPhrase.add(nextWord)
            }
        }
        var out = ""
        for (s in newPhrase)
            out += "$s "
        out = out.replace("#", "")
        out = removeDuplicateWords(out)
        return out
    }
}