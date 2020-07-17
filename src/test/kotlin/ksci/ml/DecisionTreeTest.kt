package ksci.ml

import ksci.DataFrame
import ksci.DataType as DT
import org.junit.jupiter.api.Test

class DecisionTreeTest {
    @Test
    fun titanic() {
        val frame = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("titanic-train.csv").path,
            listOf(DT.INT, DT.INT, DT.INT, DT.STRING, DT.STRING, DT.FLOAT, DT.INT, DT.INT, DT.STRING, DT.FLOAT, DT.STRING, DT.STRING)
        )

        frame.remove("Ticket")
        frame.remove("Name")
        frame.remove("Cabin")
        frame.remove("PassengerId")
        frame.remove("SibSp")
        frame.remove("Parch")
        val label = frame.remove("Survived")
        val age = frame["Age"]
        age.replaceNull(age.mean()!!.toFloat())
        frame["Age"] = age.map<Float, Int> {
            when {
                it == null -> 0
                0 < it && it < 12 -> 1
                12.0 <= it && it < 25 -> 2
                25.0 <= it && it < 55 -> 3
                55 <= it -> 4
                else -> throw RuntimeException("Invalid age: $it")
            }
        }

        frame.remove("Fare")
//        val fare = frame["Fare"]
//        frame["Fare"] = fare.map<Float, Int> {
//            when {
//                it == null -> 0
//                0 <= it && it < 7 -> 1
//                7 <= it && it < 9 -> 2
//                9 <= it && it < 14 -> 3
//                14 <= it && it < 50 -> 4
//                50 <= it && it < 100 -> 5
//                else -> 6
//            }
//        }

        val model = DecisionTree()
        model.fit(frame, label)

        val pred = model.predict(frame)
        var same = 0
        label.data.zip(pred).forEach { (l, y) ->
            if (l == y) same++
        }
        println((same.toFloat() / pred.size))

        println(model.toJson())

        val frame1 = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("titanic-test.csv").path,
            listOf(DT.INT, DT.INT, DT.STRING, DT.STRING, DT.FLOAT, DT.INT, DT.INT, DT.STRING, DT.FLOAT, DT.STRING, DT.STRING)
        )

        frame1.remove("Ticket")
        frame1.remove("Name")
        frame1.remove("Cabin")
        val passengerIds = frame1.remove("PassengerId")
        frame1.remove("SibSp")
        frame1.remove("Parch")
        frame1.remove("Fare")

        val age1 = frame1["Age"]
        age1.replaceNull(age1.mean()!!.toFloat())
        frame1["Age"] = age1.map<Float, Int> {
            when {
                it == null -> 0
                0 < it && it < 12 -> 1
                12.0 <= it && it < 25 -> 2
                25.0 <= it && it < 55 -> 3
                55 <= it -> 4
                else -> throw RuntimeException("Invalid age: $it")
            }
        }

        val pred1 = model.predict(frame)
        println("PassengerId,Survived")
        passengerIds.data.forEachIndexed { index, it ->
            println("$it,${pred1[index]}")
        }
        println("shit")
    }
}
