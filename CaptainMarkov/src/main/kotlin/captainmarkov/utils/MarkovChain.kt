package captainmarkov.utils

import captainmarkov.gui.ChainBuilder

import java.util.*

class MarkovChain {
    // Hashmap
    private var chain = Hashtable<String, Vector<String>>()
    private val rand = Random()

    /**
     * Initialize empty chain
     */
    constructor() {
        createStarterEntries()
    }

    /**
     * Initialize chain with lines
     */
    constructor(lines: List<String>) {
        createStarterEntries()
        addFile(lines)
    }

    private fun createStarterEntries() {
        // Create the first two entries (k:_start, k:_end)
        chain["_start"] = Vector()
        chain["_end"] = Vector()
    }

    /**
     * Add more lines to the Markov Chain
     *
     * @param lines List<String>
    </String> */
    fun addFile(lines: List<String>) {
        for (s in lines)
            addWords(s)
    }

    /**
     * Merge a MarkovChain with another MarkovChain
     * Pretty sure that this works...
     *
     * @param that MarkovChain
     */
    fun merge(that: MarkovChain) {
        val thatChain = that.chain
        val newChain = Hashtable<String, Vector<String>>()
        val thisKeys = chain.keys
        val thatKeys = thatChain.keys
        val keys = HashSet<String>()
        keys.addAll(thisKeys)
        keys.addAll(thatKeys)
        for (key in keys) {
            val newPair = Vector<String>()
            val thisPair = chain[key]
            val thatPair = thatChain[key]
            if (thisPair != null) newPair.addAll(thisPair)
            if (thatPair != null) newPair.addAll(thatPair)
            chain.remove(key)
            thatChain.remove(key)
            newChain[key] = newPair
        }

        chain = newChain
    }

    /**
     * Splits the phrase into groups of two, with overlap
     * This is for achieving a chain length of three
     *
     * @param phrase String
     * @return String[]
     */
    private fun spliterator(phrase: String): Array<String> {
        val split = phrase.split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return Array(split.size - 1) {
            var temp = split[it]
            temp += " " + split[it + 1]
            temp
        }
    }

    /**
     * Removes every other word in the phrase
     *
     * @param phrase String
     * @return String
     */
    private fun removeDuplicateWords(phrase: String): String {
        val words = phrase.split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var out = ""
        var add = true
        for (s in words) {
            if (add) {
                out += "$s "
                add = false
            } else {
                add = true
            }
        }
        return if (words.size % 2 == 0) out + words[words.size - 1] else out

    }

    /**
     * Add words from phrase to chain
     *
     * @param phrase String
     */

    fun addWords(phrase: String) {
        if (phrase == "#" || phrase == "") return
        // put each word into an array
        val words = spliterator(phrase)
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