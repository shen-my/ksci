package ksci.ml

import ksci.DataFrame
import ksci.DataType as DT
import org.junit.jupiter.api.Test

class SimpleBayesTest {
    @Test
    fun imdb() {
        val model = SimpleBayes()
        val frame = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("IMDB-review.csv").path,
            listOf(DT.STRING, DT.STRING)
        )

        val labels = mutableListOf<Int>()
        frame.forEach {
            val label = if (it["sentiment"] == "positive") 1 else 0
            labels.add(label)
            model.fit(it["review"] as String, label)
        }

        val predicts = mutableListOf<Int>()
        frame.forEach {
            predicts.add(model.predict(it["review"] as String))
        }

        var correctCount = 0
        var errorCount = 0
        for ((a, b) in labels.zip(predicts)) {
            if (a == b)
                correctCount++
            if (b == -1)
                errorCount++
        }

        println("$correctCount/$errorCount/${labels.size}")
    }
}
