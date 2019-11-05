package cz.woitee.endlessRunners.utils

import java.util.*

/**
 * Gets n-th object from beginning. Note, not very fast, use scarsely. Meant only for debugging purposes.
 */
fun <T> ArrayDeque<T>.findFromBeginning(n: Int): T {
    val it = this.iterator()
    for (i in 1 until n) {
        it.next()
    }
    return it.next()
}

/**
 * Gets n-th object from end. Note, not very fast, use scarsely. Meant only for debugging purposes.
 */
fun <T> ArrayDeque<T>.findFromEnd(n: Int): T {
    val it = this.descendingIterator()
    for (i in 1 until n) {
        it.next()
    }
    return it.next()
}
