package cz.woitee.endlessRunners.utils

import java.util.ArrayDeque

fun <T> ArrayDeque<T>.findFromBeginning(n: Int): T {
    val it = this.iterator()
    for (i in 1 until n) {
        it.next()
    }
    return it.next()
}

fun <T> ArrayDeque<T>.findFromEnd(n: Int): T {
    val it = this.descendingIterator()
    for (i in 1 until n) {
        it.next()
    }
    return it.next()
}
