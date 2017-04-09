package Utils

import java.util.*

/**
 * Created by woitee on 15/01/2017.
 */

fun <T> arrayList(size: Int, factory: () -> T): ArrayList<T> {
    val res = ArrayList<T>(size)
    for (i in 1 .. size) {
        res.add(factory())
    }
    return res
}

/**
    Shifts all the elements either left (for positive amount) or right (for negative amount), filling the blank space
    with results from given factory.
 */
fun <T> ArrayList<T>.shift(amount: Int, factory: ()->T) {
    if (Math.abs(amount) > this.size) {
        for (i in 0 .. this.lastIndex)
            this[i] = factory()
        return
    }
    if (amount > 0) {
        for (i in 0 .. this.size - amount - 1) {
            this[i] = this[i + amount]
        }
        for (i in this.size - amount .. this.lastIndex) {
            this[i] = factory()
        }
    } else {
        for (i in this.lastIndex downTo -amount) {
            this[i] = this[i + amount]
        }
        for (i in 0 .. -amount - 1) {
            this[i] = factory()
        }
    }
}

fun <T> ArrayList<T>.reverse(): ArrayList<T> {
    for (i in 0 .. (this.size / 2) - 1) {
        val tmp = this[i]
        this[i] = this[this.lastIndex - i]
        this[this.lastIndex - i] = tmp
    }
    return this
}

fun <T> ArrayList<T>.pop(): T {
    return this.removeAt(this.count() - 1)
}