package ksci.extension

inline fun <reified T> array2D(d1: Int, d2: Int, init: (Int, Int) -> T): Array<Array<T>> {
    return Array(d1) { i -> Array<T>(d2) { j -> init(i, j) } }
}

fun intArray2D(d1: Int, d2: Int) = array2D<Int>(d1, d2) { _, _ -> 0 }
