package captainmarkov.utils

import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

class HtmlToPlainText {

    /**
     * Format an Element to plain-text
     * @param element the root element to format
     * @return formatted text
     */
    fun getPlainText(element: Element): String {
        val formatter = FormattingVisitor()
        NodeTraversor.traverse(formatter, element) // walk the DOM, and call .head() and .tail() for each node

        return formatter.toString()
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private class FormattingVisitor : NodeVisitor {

        private val accumulatedText = StringBuilder() // holds the accumulated text

        // hit when the node is first seen
        override fun head(node: Node, depth: Int) {
            val name = node.nodeName()
            when {
                name == "a" -> Unit
                node is TextNode -> append(node.text()) // TextNodes carry all user-readable text in the DOM.
                name == "li" -> append("\n * ")
                name == "dt" -> append("  ")
                StringUtil.`in`(name, "p", "h1", "h2", "h3", "h4", "h5", "tr") -> append("\n")
            }
        }

        // hit when all of the node's children (if any) have been visited
        override fun tail(node: Node, depth: Int) {
            val name = node.nodeName()
            if (StringUtil.`in`(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5"))
                append("\n")
        }

        // appends text to the string builder with a simple word wrap method
        private fun append(text: String) {
            if (text == " " && (accumulatedText.isEmpty() || arrayOf(' ', '\n').contains(accumulatedText.last())))
                return  // don't accumulate long runs of empty spaces
            accumulatedText.append(text)
        }

        override fun toString(): String {
            return accumulatedText.toString()
        }
    }
}