package ksci

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser

class DataFrame {
    private val titles = mutableListOf<String>()

    private val columns = mutableListOf<DataColumn>()

    val columnNames: List<String> = titles

    val count
        get() = if (columns.isEmpty()) 0 else columns[0].count

    val indices
        get() = 0 until count

    fun isEmpty() = columns.isEmpty() || columns[0].isEmpty()

    fun column(index: Int): DataColumn {
        return columns[index]
    }

    fun column(name: String): DataColumn {
        return columns[columnNames.indexOf(name)]
    }

    fun forEachColumn(acceptor: (Pair<String, DataColumn>) -> Unit) {
        columnNames.zip(columns).forEach(acceptor)
    }

    fun forEach(acceptor: (Map<String, Any?>) -> Unit) {
        indices.forEach {
            acceptor(get(it))
        }
    }

    fun remove(name: String): DataColumn {
        if (name !in titles) throw IllegalArgumentException("Can not find column with name($name)")
        val index = titles.indexOf(name)
        titles.removeAt(index)
        return columns.removeAt(index)
    }

    operator fun get(index: Int): Map<String, Any?> {
        if (index !in indices) throw ArrayIndexOutOfBoundsException("$index exceed [0, $count)")
        return titles.zip(columns.map { it.get<Any>(index) }).toMap()
    }

    operator fun get(indices: Collection<Int>): DataFrame {
        val frame = DataFrame()
        forEachColumn { (name, column) ->
            val newColumn = DataColumn(column.type)
            for (i in indices)
                newColumn.add(column[i])
            frame.addColumn(name, newColumn)
        }
        return frame
    }

    operator fun get(name: String) = column(name)

    operator fun set(name: String, column: DataColumn) {
        val index = titles.indexOf(name)
        if (index == -1) {
            addColumn(name, column)
        } else {
            columns[index] = column
        }
    }

    fun <T> map(transformer: (Map<String, Any?>) -> T): List<T> {
        return indices.map(::get).map(transformer)
    }

    fun addColumn(name: String, column: DataColumn) {
        if (columns.isNotEmpty() && column.count != count)
            throw IllegalArgumentException("Column count(${column.count}) diff with frame count($count)")
        titles.add(name)
        columns.add(column)
    }

    fun add(values: List<Any?>) {
        if (values.size != columns.size)
            throw IllegalArgumentException("Input value count(${values.size}) not sufficient column count($count)")
        columns.zip(values).forEach { (column, value) ->
            column.add(value)
        }
    }

    companion object {
        fun loadCSV(path: String, dTypes: List<DataType>, titles: List<String> = listOf()): DataFrame {
            val reader = BufferedReader(FileReader(path))
            val firstLine = reader.readLine()
            val mTitles = mutableListOf<String>()
            if (titles.isEmpty()) {
                if (firstLine == null) throw IOException("File is empty")
                mTitles.addAll(firstLine.split(','))
            } else
                mTitles.addAll(titles)

            if (dTypes.size < titles.size)
                throw RuntimeException("Title count(${titles.size} larger than dType count(${dTypes.size}")

            val frame = DataFrame()
            mTitles.zip(dTypes).forEach { (name, type) ->
                frame.addColumn(name, DataColumn(type))
            }

            val parse = CSVParser.parse(reader, CSVFormat.DEFAULT)
            for (record in parse.records) {
                val values = dTypes.mapIndexed { index, dType ->
                    if (record[index].isEmpty())
                        null
                    else
                        dType.fromString(record[index])
                }
                frame.add(values)
            }

            return frame
        }
    }
}

