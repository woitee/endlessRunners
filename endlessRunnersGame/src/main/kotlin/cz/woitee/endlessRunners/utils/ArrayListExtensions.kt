package cz.woitee.endlessRunners.utils

import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Some extension methods to provide wider options on how to work with ArrayList.
 *
 * Created by woitee on 15/01/2017.
 */

/**
 * Initializes a new ArrayList of given size by calling the factory.
 */
fun <T> arrayList(size: Int, factory: (Int) -> T): ArrayList<T> {
    val res = ArrayList<T>(size)
    for (i in 0 until size) {
        res.add(factory(i))
    }
    return res
}

/**
 * Shrinks or grows the ArrayList to given size. If new elements are needed,
 * the factory is called.
 */
fun <T> ArrayList<T>.resizeTo(size: Int, factory: () -> T): ArrayList<T> {
    while (this.size > size) {
        this.removeAt(this.size - 1)
    }
    while (this.size < size) {
        this.add(factory())
    }
    return this
}

/**
 * Shifts all the elements either right (for positive amount) or left (for negative amount), filling the blank space
 * with results from given factory.
 */
fun <T> ArrayList<T>.shift(amount: Int, factory: () -> T) {
    if (Math.abs(amount) > this.size) {
        for (i in 0 .. this.lastIndex)
            this[i] = factory()
        return
    }
    if (amount > 0) {
        for (i in this.lastIndex downTo amount) {
            this[i] = this[i - amount]
        }
        for (i in 0 until amount) {
            this[i] = factory()
        }
    } else {
        for (i in 0 until this.size + amount) {
            this[i] = this[i - amount]
        }
        for (i in this.size + amount .. this.lastIndex) {
            this[i] = factory()
        }
    }
}

/**
 * Reverses an arrayList in place.
 */
fun <T> ArrayList<T>.reverse(): ArrayList<T> {
    for (i in 0 until this.size / 2) {
        val tmp = this[i]
        this[i] = this[this.lastIndex - i]
        this[this.lastIndex - i] = tmp
    }
    return this
}

/**
 * A more useful pop method when using an ArrayList as a stack.
 */

fun <T> ArrayList<T>.pop(): T {
    return this.removeAt(this.count() - 1)
}

/**
 * Get a random element of the arrayList.
 */
fun <T> ArrayList<T>.randomElement(random: Random? = null): T {
    val rand = (random ?: ThreadLocalRandom.current())
    return this[rand.nextInt(this.count())]
}

/**
 * A utility method to create a new ArrayList without an element in the middle.
 */
fun <T> ArrayList<T>.except(index: Int): List<T> {
    return this.slice((0 until index).union(index + 1 until this.count()))
}

/**
 * A method that can set elements either in, or just at the end of the list.
 */
fun <T> ArrayList<T>.addOrPut(index: Int, obj: T): ArrayList<T> {
    if (index == this.count())
        this.add(obj)
    else
        this[index] = obj
    return this
}
