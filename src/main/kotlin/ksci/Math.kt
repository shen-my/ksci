package ksci

import kotlin.math.absoluteValue

object Math {
    // val DOUBLE_PRECISION = 1e-4

    // 精度比标准库略低，性能差 30 倍以上
    fun sqrt(a: Double): Double {
        a < 0 && return Double.NaN
        a == .0 && return .0

        var root = if (a > 1) a / 2 else a + (1 - a) / 2
        var lastError = Double.MAX_VALUE
        var error = (root * root - a).absoluteValue
        while (error < lastError) {
            root -= (root / 2 - a / (2 * root))
            lastError = error
            error = (root * root - a).absoluteValue
        }
        return root
    }
}
