package cz.woitee.endlessRunners.utils

/**
 * Approximate equality for doubles.
 */
fun apxEquals(a: Double, b: Double, epsilon: Double = 0.0001): Boolean {
    return a == b || Math.abs(a - b) < epsilon
}

/**
 * Formatting for doubles with a specific number of digits after decimal point.
 */
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)!!
