package ksci.ml

import ksci.DataColumn
import ksci.DataFrame
import java.lang.IllegalArgumentException

enum class PredictAlg {
    MODE, MEAN
}

class KNN(
    private val X: DataFrame,
    private val Y: DataColumn,
    private val distanceF: (Map<String, Any?>, Map<String, Any?>) -> Float,
    private val K: Int = 3,
    private val predictAlg: PredictAlg = PredictAlg.MODE
) {
    init {
        if (K <= 0) throw IllegalArgumentException("K($K) less than 1")
    }

    fun predict(vector: Map<String, Any?>): Any? {
        assert(X.count >= K)
        assert(Y.count >= K)

        val distances = mutableListOf<Pair<Int, Float>>()
        for (i in X.indices) {
            distances.add(i to distanceF(X[i], vector))
        }
        distances.sortBy { it.second }
        val indices = distances.subList(0, K).map { it.first }
        return when (predictAlg) {
            PredictAlg.MODE -> Y[indices].mode<Any?>()[0]
            PredictAlg.MEAN -> Y[indices].mean()
        }
    }
}
