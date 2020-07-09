package ksci

import java.lang.RuntimeException
import kotlin.math.log2

open class DataColumn(val type: DataType, val data: MutableList<Any?> = mutableListOf()) {

    constructor(type: DataType, data: Collection<Any?>): this(type, data.toMutableList())

    val count: Int
        get() = data.size

    /**
     * 是否是单值的
     */
    fun singled(): Boolean {
        if (data.isEmpty()) return false
        return data.all { it == data[0] }
    }

    fun isEmpty() = data.isEmpty()

    fun group(): Map<Any?, List<Int>> {
        val groups = mutableMapOf<Any?, MutableList<Int>>()
        for (i in data.indices) {
            groups.getOrPut(data[i], ::mutableListOf).add(i)
        }
        return groups
    }

    inline operator fun <reified T> get(index: Int): T? {
        return data[index] as T?
    }

    operator fun get(indices: Collection<Int>): DataColumn {
        val newer = DataColumn(type)
        for (i in indices)
            newer.data.add(data[i])
        return newer
    }

    /**
     * 返回集合的众数
     */
    inline fun <reified T> mode(): List<T?> {
        if (data.isEmpty()) return emptyList()

        val sortedValueCount = group()
            .map { (value, indices) -> value to indices.size }
            .sortedByDescending { it.second }
        return sortedValueCount
            .filter { it.second == sortedValueCount[0].second }
            .map { it.first as T? }
    }

    fun mean(): Double? {
        if (type !in listOf(DataType.INT, DataType.FLOAT, DataType.DOUBLE))
            throw RuntimeException("Can not calc mean for $type type values ")

        if (data.isEmpty() || data.all { it == null })
            return null

        var count = 0
        var total = .0
        for (value in data) {
            value?.also {
                count++
                total += (value as Number).toDouble()
            }
        }
        return total / count
    }

    fun entropy(): Float {
        if (data.isEmpty()) return 0f
        return group().values
            .map {
                val prob = it.size.toFloat() / count
                -prob * log2(prob)
            }.reduce(Float::plus)
    }

    fun add(x: Any?) {
        if (!type.accept(x))
            throw IllegalArgumentException("$type column can not accept ${x!!::class}")
        data.add(x)
    }

    fun replaceNull(x: Any) {
        if (!type.accept(x))
            throw IllegalArgumentException("$type column can not accept ${x::class}")
        for (i in data.indices) {
            data[i] = data[i] ?: x
        }
    }

    inline fun <reified T> transfer(transformer: (T?) -> T?) {
        for (i in data.indices) {
            data[i] = transformer(data[i] as T?)
        }
    }

    inline fun <reified F, reified T> map(transformer: (F?) -> T?): DataColumn {
        val newer = DataColumn(DataType.valueOf<T>())
        data.forEach {
            newer.add(transformer(it as F?))
        }
        return newer
    }
}
