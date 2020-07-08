package ksci

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.*

typealias Func1 = (Int) -> Boolean

class NDArrayTest {
    @Test fun `test shape`() {
        assertEquals(1, Shape().size)
        assertEquals(1, Shape(1).size)
        assertEquals(6, Shape(1, 2, 3).size)

        val shape = Shape(3, 4, 5)
        assertEquals(3, shape.coordToIndex(0, 0, 3))
        assertEquals(12, shape.coordToIndex(0, 2, 2))
        assertEquals(52, shape.coordToIndex(2, 2, 2))

        assertEquals(39, shape.coordToIndex(1, -1, -1))
        assertEquals(40, shape.coordToIndex(2, -4, -5))

        assertArrayEquals(intArrayOf(0, 0, 3), shape.indexToCoord(3))
        assertArrayEquals(intArrayOf(0, 2, 2), shape.indexToCoord(12))
        assertArrayEquals(intArrayOf(2, 2, 2), shape.indexToCoord(52))

        assertThrows(IndexOutOfBoundsException::class.java) { shape.coordToIndex(0, 2, 8) }

        assertThrows(IndexOutOfBoundsException::class.java) { shape.indexToCoord(-1) }
        assertThrows(IndexOutOfBoundsException::class.java) { shape.indexToCoord(1024) }

        val nd = NDArray.array(arrayOf(1, 2, 3, 4))
        nd.reshape(1, 2, 2, 1)
        assertEquals(4, nd.ndim)
        assertEquals(3, nd[0].ndim)
        assertEquals(2, nd[0, 0].ndim)
        assertEquals(1, nd[0][0, 0].ndim)
        assertEquals(0, nd[0, 0, 0, 0].ndim)
    }

    @Test fun `test NDArray eye`() {
        val nd = NDArray.eye(3)
        assertEquals(1.0f, nd[0, 0].scalar())
        assertEquals(0.0f, nd[0, 1].scalar())
        assertEquals(1.0f, nd[1, 1].scalar())
    }

    @Test fun `test NDArray range`() {
        val nd = NDArray.range(0, 12)
        nd.reshape(3, 4)
        assertEquals(2.0f, nd[0, 2].scalar())
        assertEquals(7.0f, nd[1, 3].scalar())
        assertEquals(7.0f, nd[1][3].scalar())
        assertEquals(8.0f, nd[2][0].scalar())
    }

    @Test fun `test uniform random`() {
        val a = 10f
        val b = 1f
        val x = NDArray.uniform(0f, 10f, Shape(10))
        val y = x * a + b

        x.forEach { print(it); print(' ') }
        println()
        y.forEach { print(it); print(' ') }
    }
}
