package ksci.extension

inline fun <reified T> array2D(d1: Int, d2: Int, init: (Int, Int) -> T): Array<Array<T>> {
    return Array(d1) { i -> Array<T>(d2) { j -> init(i, j) } }
}

fun intArray2D(d1: Int, d2: Int) = array2D<Int>(d1, d2) { _, _ -> 0 }

fun <E> List<E>.truncate(atMost: Int): List<E> {
    if (atMost > this.size)
        return this
    return this.slice(0 until atMost)
}
