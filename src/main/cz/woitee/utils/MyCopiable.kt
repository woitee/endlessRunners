package cz.woitee.utils

interface MyCopiable<T: MyCopiable<T>> {
    fun makeCopy(): T
}