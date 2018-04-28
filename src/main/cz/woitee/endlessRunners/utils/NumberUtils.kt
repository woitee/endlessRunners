package cz.woitee.endlessRunners.utils

object NumberUtils {
    fun apxEquals(a: Double, b: Double, epsilon: Double = 0.0001): Boolean {
        return a == b || Math.abs(a - b) < epsilon
    }
}