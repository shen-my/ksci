package ksci

import kotlin.random.Random

class Shape(vararg val dims: Int) {
    val ndim = dims.size
    val size: Int

    init {
        dims.forEach { if (it <= 0) throw IllegalArgumentException("Dim must bigger than 0") }
        size = if (dims.isEmpty()) 1 else dims.reduce(Int::times)
    }

    operator fun get(d: Int) = dims[d]

    /**
     * 检查维度是否支持广播
     */
    internal fun compatble(other: Shape): Boolean {
        return ndim >= other.ndim && dims.sliceArray((ndim - other.ndim) until ndim).contentEquals(other.dims)
    }

    internal fun indexToCoord(i: Int): IntArray {
        if (i < 0 || i >= size) throw IndexOutOfBoundsException("Index($i) exceed range[0, $size)")
        val coord = IntArray(ndim)
        var index = i
        for (d in (ndim - 1) downTo 0) {
            coord[d] = index % dims[d]
            index -= coord[d]
            index /= dims[d]
        }
        return coord
    }

    internal fun coordToIndex(vararg coord: Int): Int {
        if (coord.size > ndim) throw IllegalArgumentException("Coord dimension(${coord.size}) large than shape dimension($ndim)")
        for (i in coord.indices) {
            if (coord[i] >= dims[i] || coord[i] < -dims[i])
                throw IndexOutOfBoundsException("The ${i}th dimension coord exceed range[${-dims[i]}, ${dims[i]})")
            if (coord[i] < 0)
                coord[i] += dims[i]
        }
        var index = 0
        var power = 1
        for (a in (ndim - 1) downTo 0) {
            index += power * coord.getOrElse(a) { 0 }
            power *= dims[a]
        }
        return index
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            !is Shape -> false
            else -> dims.contentEquals(other.dims)
        }
    }

    override fun toString(): String {
        return dims.joinToString(prefix="Shape(", postfix=")")
    }
}

class NDArray private constructor(private val array: FloatArray, var shape: Shape, private val offset: Int = 0) {
    val ndim get() = shape.ndim
    val size get() = shape.size

    private constructor(array: FloatArray) : this(array, Shape(array.size))
    private constructor(size: Int, initFun: (Int) -> Float = { _ -> 0.0f }) : this(FloatArray(size, initFun))

    init {
        assert(offset + shape.size <= array.size)
    }

    operator fun get(vararg coord: Int): NDArray {
        if (coord.size > ndim) throw IllegalArgumentException("Coordinate dimension(${coord.size}) exceed array dimension($ndim)")
        val _offset = offset + shape.coordToIndex(*coord)
        val dims = shape.dims.sliceArray(coord.size until shape.ndim)

        return NDArray(array, Shape(*dims), _offset)
    }

    private fun at(i: Int): Float {
        assert(i < size)
        return array[offset + i]
    }

    fun scalar(): Float {
        if (size != 1) throw IllegalStateException("Array is a vector so can not be convert to scalar")
        return array[offset]
    }

    fun reshape(newShape: Shape) {
        if (newShape.size != shape.size) throw IllegalArgumentException("New $newShape does not compatible to old $shape")
        this.shape = newShape
    }

    fun reshape(vararg dims: Int) = reshape(Shape(*dims))

    fun forEach(func: (Float) -> Unit) {
        for (i in 0 until size)
            func(at(i))
    }
    fun map(func: (Float, IntArray) -> Float): NDArray {
        return NDArray(size) {
            func(array[offset + it], shape.indexToCoord(it))
        }
    }

    fun map(func: (Float) -> Float): NDArray {
        return NDArray(size) {
            func(array[offset + it])
        }
    }

    /**
     * 执行和另一数组的二元算数操作
     */
    private fun binaryArithmetic(other: NDArray, op: (Float, Float) -> Float): NDArray {
        return if (ndim == other.ndim) {
            if (shape != other.shape)
                throw IllegalStateException("Righter ${other.shape} not match with lefter $shape")
            NDArray(size) { op(at(it), other.at(it)) }
        } else {
            val (larger, smaller) = if (this.ndim > other.ndim) this to other else other to this
            if (!larger.shape.compatble(smaller.shape))
                throw IllegalStateException("Righter ${other.shape} not compatible with lefter $shape")
            NDArray(size) {
                op(larger.at(it), smaller.at(it % smaller.size))
            }
        }
    }

    operator fun plus(other: Float) = map { it: Float -> it + other }

    operator fun plus(other: NDArray) = binaryArithmetic(other, Float::plus)

    operator fun times(other: Float) = map { it: Float -> it * other }

    operator fun times(other: NDArray) = binaryArithmetic(other, Float::times)

    override fun toString(): String {
        fun printVector(vector: NDArray) {
            buildString {
                append('[')
                for (i in 0 until vector.size - 1) {
                    append(vector[i].scalar())
                    append(", ")
                }
                append(vector[-1])
                append(']')
            }
        }
        TODO()
    }

    companion object {
        /** 输出时每个维度的最大个数 */
        var max_print_count = 3

        private fun initArray(shape: Shape, initFun: (IntArray) -> Float = { _ -> 0.0f }): FloatArray {
            return FloatArray(shape.size) {
                initFun(shape.indexToCoord(it))
            }
        }

        fun <T : Number> array(items: Array<T>): NDArray {
            return NDArray(
                items.map { it.toFloat() }.toFloatArray()
            )
        }

        fun zeros(vararg dims: Int): NDArray {
            val shape = Shape(*dims)
            return NDArray(initArray(shape), shape)
        }

        fun range(start: Int = 0, end: Int): NDArray {
            if (start >= end) throw IllegalArgumentException("Start($start} should bigger than end($end)")
            val array = FloatArray(end - start) { (it + start).toFloat() }
            return NDArray(array, Shape(end - start))
        }

        fun eye(dim: Int): NDArray {
            val shape = Shape(dim, dim)
            val array = initArray(shape) { index ->
                val i = index[0]
                val j = index[1]
                if (i == j) 1.0f else 0.0f
            }
            return NDArray(array, shape)
        }

        /**
         * 均匀分布
         */
        fun uniform(a: Float = 0.0f, b: Float = 1.0f, shape: Shape = Shape(1)): NDArray {
            val array = FloatArray(shape.size) {
                Random.nextFloat() * (b - a) + a
            }
            return NDArray(array, shape)
        }
    }
}
