package cz.woitee.endlessRunners.evolution.utils

import org.junit.jupiter.api.Assertions.*

class MathUtilsTest {
    @org.junit.jupiter.api.Test
    fun divisorsTest() {
        assertEquals(
            arrayListOf(2, 2, 3),
            MathUtils.divisorsOf(12)
        )

        assertEquals(
            arrayListOf(2, 2, 2, 2, 3, 5),
            MathUtils.divisorsOf(240)
        )

        assertEquals(
            arrayListOf(7, 7),
            MathUtils.divisorsOf(49)
        )
    }

    @org.junit.jupiter.api.Test
    fun allDivisorsTest() {
        assertEquals(
            arrayListOf(1, 2, 3, 4, 6, 12),
            MathUtils.allDivisorsOf(12)
        )
        assertEquals(
            arrayListOf(1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 16, 20, 24, 30, 40, 48, 60, 80, 120, 240),
            MathUtils.allDivisorsOf(240)
        )
    }
}
