package ksci

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import ksci.DataType as DT

class DataFrameTest {
    @Test fun `load empty csv`() {
        val frame = DataFrame.loadCSV(
            this::class.java.classLoader.getResource("train.csv").path,
            listOf(DT.INT, DT.INT, DT.INT, DT.STRING, DT.STRING, DT.FLOAT, DT.INT, DT.INT, DT.STRING, DT.FLOAT, DT.STRING, DT.STRING)
        )

        assertEquals(891, frame.count)
    }
}
