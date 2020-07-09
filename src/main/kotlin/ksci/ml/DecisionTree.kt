package ksci.ml

import com.google.gson.Gson
import ksci.DataColumn
import ksci.DataFrame

internal abstract class Node

internal class Branch(private val property: String, private val router: Map<Any?, Node>) : Node() {
    fun route(data: Map<String, Any?>): Node {
        (data[property] ?: throw RuntimeException("Data do not has property($property)"))
            .also {
                return router[it] ?: throw RuntimeException("Unknown how to dispose value($it) on property($property)")
            }
    }
}

internal class Leaf(val value: Any) : Node()

const val ENTROPY_GAIN_THRESHOLD = 0.05

class DecisionTree {

    private lateinit var root: Node

    private val usedProperties = mutableListOf<String>()

    fun fit(x: DataFrame, y: DataColumn) {
        root = build(x, y)
    }

    private fun build(x: DataFrame, y: DataColumn): Node {
        if (y.singled())
            return Leaf(y[0]!!)

        if (x.isEmpty())
            return Leaf(y.mode<Any>()[0]!!)

        var minEntropy = Float.MAX_VALUE
        var bestColumn = x.columnNames[0]

        x.forEachColumn { (name, column) ->
            if (name in usedProperties)
                return@forEachColumn

            val entropy = column.group().map { (_, indices) ->
                y[indices].entropy() * (indices.size / x.count.toFloat())
            }.reduce(Float::plus)

            if (entropy < minEntropy) {
                minEntropy = entropy
                bestColumn = name
            }
        }

        val entropyGain = y.entropy() - minEntropy
        if (entropyGain < ENTROPY_GAIN_THRESHOLD)
            return Leaf(y.mode<Any>()[0]!!)

        val decisionColumn = x[bestColumn]
        usedProperties.add(bestColumn)
        val router = decisionColumn.group().mapValues { (_, indices) ->
            build(x[indices], y[indices])
        }

        return Branch(bestColumn, router)
    }

    fun predict(x: DataFrame): List<Any> = x.map(::predict)

    fun predict(x: Map<String, Any?>): Any {
        var iter = root
        while (iter !is Leaf) {
            iter = (iter as Branch).route(x)
        }
        return (iter).value
    }

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
