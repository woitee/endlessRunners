package cz.woitee.endlessRunners.utils.pools

/**
 * A simple interface to provide objects in our pooling.
 */
abstract class SimpleFactory<T> {
    /**
     * Creates a new instance of an objects.
     */
    abstract fun create(): T

    /**
     * Resets the object back to default state.
     */
    open fun passivateObject(obj: T) {}
}
