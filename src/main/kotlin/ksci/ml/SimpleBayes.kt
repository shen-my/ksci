package ksci.ml

import ksci.extension.truncate
import java.util.StringTokenizer
import kotlin.math.max

class SimpleBayes {
    val labelCount = mutableMapOf<Int, Int>()
    val labelWordCount = mutableMapOf<Pair<Int, String>, Int>()
    var totalDocCount = 0

    val neutralWords = listOf(
        "i", "is", "and", "or", "with", "on", "my", "it", "that", "but", "of", "to", "in",
        "are", "was", "has", "had", "there", "for", "a", "being", "be", "the", "so", "as",
        "me", "he", "she", "this", "those", "one", "do", "his", "her", "our", "then", "you",
        "by", "their", "go", "been", "into", "while", "at", "us", "we", "mr", "these", "what",
        "which", "them", "own", "have", "they", "an", "let", "got", "way", "its", "man", "went"
    )

    fun fit(text: String, label: Int) {
        getWords(text).toSet().forEach { word ->
            labelWordCount[label to word] = labelWordCount.getOrDefault(label to word, 0) + 1
        }
        labelCount[label] = labelCount.getOrDefault(label, 0) + 1
        totalDocCount++
    }

    fun predict(text: String): Int {
        var maxProb = 0f
        var maxLabel = -1
        val words = getWords(text).toSet()
        for ((label, labelNum) in labelCount) {
            val prob = words
                .map { (0.5f + (labelWordCount[label to it] ?: 0)) / labelNum }
                .sortedDescending()
                .truncate(25)
                .fold(labelNum.toFloat() / totalDocCount, Float::times)

            if (prob > maxProb) {
                maxProb = prob
                maxLabel = label
            }
        }
        return maxLabel
    }

    private fun getWords(text: String): List<String> {
        val txt = text.replace(Regex("(<br ?/>)|('s)|\\d"), " ")
        return StringTokenizer(txt, " \t\r\n\r.,()\":'")
            .toList()
            .map { it.toString().toLowerCase() }
            .filter {
                it.length > 1 && (it !in neutralWords)
            }
    }
}
