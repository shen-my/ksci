package ksci.extension

import kotlin.test.Test
import kotlin.test.assertEquals
import ksci.extension.*

class StringTest {
    @Test fun `test get`() {
        val s = "abcdef"
        assertEquals(s[0..-1], s)
        assertEquals(s[-3..-1], "def")
        assertEquals(s[-5..2], "bc")
    }
}
