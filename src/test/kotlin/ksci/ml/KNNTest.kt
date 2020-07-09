package ksci.ml

import ksci.DataColumn
import ksci.DataFrame
import ksci.DataType as DT
import ksci.Normalization
import org.junit.jupiter.api.Test


class KNNTest {
    @Test
    fun titanic() {
        val frame = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("titanic-train.csv").path,
            listOf(DT.INT, DT.INT, DT.INT, DT.STRING, DT.STRING, DT.FLOAT, DT.INT, DT.INT, DT.STRING, DT.FLOAT, DT.STRING, DT.STRING)
        )

        frame.remove("PassengerId")
        val label = frame.remove("Survived")
        frame.remove("Pclass")
        frame.remove("Name")
        frame.remove("SibSp")
        frame.remove("Parch")
        frame.remove("Ticket")
        frame.remove("Cabin")
        frame.remove("Embarked")

        var age = frame["Age"]
        age.replaceNull(age.mean()!!.toFloat())
        frame["Age"] = DataColumn(DT.FLOAT, Normalization.uniform(age.data as List<Float>))

        var fare = frame["Fare"]
        fare.replaceNull(fare.mode<Float>()[0]!!)
        frame["Fare"] = DataColumn(DT.FLOAT, Normalization.uniform(fare.data as List<Float>))

        frame["Sex"] = frame["Sex"].map<String, Float> {
            when (it) {
                "male" -> 0f
                "female" -> 1f
                else -> 0f
            }
        }

        val model = KNN(frame, label, { props1, props2 ->
            Distance.euclidean(
                listOf(props1["Fare"] as Float, props1["Age"] as Float, props1["Sex"] as Float),
                listOf(props2["Fare"] as Float, props2["Age"] as Float, props2["Sex"] as Float)
            )
        }, 5)

        val testFrame = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("titanic-test.csv").path,
            listOf(DT.INT, DT.INT, DT.STRING, DT.STRING, DT.FLOAT, DT.INT, DT.INT, DT.STRING, DT.FLOAT, DT.STRING, DT.STRING)
        )
        age = testFrame["Age"]
        age.replaceNull(age.mean()!!.toFloat())
        testFrame["Age"] = DataColumn(DT.FLOAT, Normalization.uniform(age.data as List<Float>))

        fare = testFrame["Fare"]
        fare.replaceNull(fare.mode<Float>()[0]!!)
        testFrame["Fare"] = DataColumn(DT.FLOAT, Normalization.uniform(fare.data as List<Float>))

        testFrame["Sex"] = testFrame["Sex"].map<String, Float> {
            when (it) {
                "male" -> 0f
                "female" -> 1f
                else -> 0f
            }
        }
        val passengerIds = testFrame.remove("PassengerId")
        println("PassengerId,Survived")
        passengerIds.data.forEachIndexed { index, it ->
            println("$it,${model.predict(testFrame[index])}")
        }
    }
}
