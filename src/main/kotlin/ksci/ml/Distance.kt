package ksci.ml

import kotlin.math.absoluteValue
import kotlin.math.sqrt

object Distance {
    /**
     * 欧氏距离，适用于数值型
     */
    fun euclidean(vec1: List<Float>, vec2: List<Float>): Float {
        if (vec1.size != vec2.size)
            throw IllegalArgumentException("Vector size different")

        var total = 0f
        for ((a, b) in vec1.zip(vec2))
            total += (a - b) * (a - b)
        return sqrt(total)
    }

    /**
     * 曼哈顿距离，用于数值型
     */
    fun manhattan(vec1: List<Float>, vec2: List<Float>): Float {
        if (vec1.size != vec2.size)
            throw IllegalArgumentException("Vector size different")

        var total = 0f
        for ((a, b) in vec1.zip(vec2))
            total += (a - b).absoluteValue
        return total
    }

    /**
     * 适用于对称的布尔类型
     */
    fun <T> simpleMatching(vec1: List<T>, vec2: List<T>): Float {
        if (vec1.size != vec2.size)
            throw IllegalArgumentException("Vector size different")

        if (vec1.isEmpty()) return 0f

        var diffCount = 0
        for ((a, b) in vec1.zip(vec2)) {
            if (a != b)
                diffCount++
        }

        return diffCount.toFloat() / vec1.size
    }

    /**
     * 适用于非对称的布尔类型
     */
    fun <T> jaccard(vec1: List<T>, vec2: List<T>, negativeValue: T): Float {
        if (vec1.size != vec2.size)
            throw IllegalArgumentException("Vector size different")

        if (vec1.isEmpty()) return 0f

        var diffCount = 0
        var sameCount = 0
        for ((a, b) in vec1.zip(vec2)) {
            if (a != b)
                diffCount++
            else if (a != negativeValue)
                sameCount++
        }

        return diffCount.toFloat() / sameCount
    }
}
