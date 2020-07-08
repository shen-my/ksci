package ksci

import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.system.measureTimeMillis

class MathTest {
    @Test fun `test sqrt`() {
        assertTrue { (kotlin.math.sqrt(2.0) - Math.sqrt(2.0)).absoluteValue < 1e-10 }
        assertTrue { (kotlin.math.sqrt(0.5) - Math.sqrt(0.5)).absoluteValue < 1e-10 }
        assertTrue { (kotlin.math.sqrt(1.0) - Math.sqrt(1.0)).absoluteValue < 1e-10 }
        assertTrue { (kotlin.math.sqrt(0.0003) - Math.sqrt(0.0003)).absoluteValue < 1e-10 }
        assertTrue { (kotlin.math.sqrt(2.5e20) - Math.sqrt(2.5e20)).absoluteValue < 1e-10 }
    }
}
