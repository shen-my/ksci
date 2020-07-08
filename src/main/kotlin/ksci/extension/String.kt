package ksci.extension

operator fun String.get(range: IntProgression): String {
    if (range.first < -this.length || range.first >= this.length)
        throw IllegalArgumentException("Range start position exclude [-length, length)")

    if (range.last < -this.length || range.last >= this.length)
        throw IllegalArgumentException("Range end position exclude [-length, length)")

    val start = if (range.first >= 0) range.first else range.first + this.length
    val end = if (range.last >= 0) range.last else range.last + this.length

    return if (end < start) "" else this.substring(start..end)
}

operator fun <T> Array<T>.get(float: Float): Int {
    TODO()
}
