package ksci

import kotlin.math.sqrt

object Statistic {
    fun mean(values: Collection<Float>): Float {
        if (values.isEmpty())
            throw IllegalArgumentException("Evaluate mean for empty collection")
        return values.reduce { a, b -> a + b } / values.size
    }

    fun std(values: Collection<Float>): Float {
        return sqrt(variance(values))
    }

    fun variance(values: Collection<Float>): Float {
        val avg = mean(values)
        return mean(values.map { (it - avg) * (it - avg) })
    }
}
