package cz.woitee.utils.pools

/**
 * Created by woitee on 04/06/2017.
 */
abstract class SimpleFactory<T> {
    abstract fun create(): T
    open fun passivateObject(obj: T) {}
}