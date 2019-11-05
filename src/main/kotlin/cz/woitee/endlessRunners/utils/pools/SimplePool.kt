package cz.woitee.endlessRunners.utils.pools

import cz.woitee.endlessRunners.utils.pop
import java.util.*

/**
 * A very simple pooling class, provide a factory and you have a pool of objects.
 * Each thread has a separate pool, as they are ment to provide speed, and locking would remove that.
 */
open class SimplePool<T> (val factory: SimpleFactory<T>) {
    val threadPoolStack = ThreadLocal.withInitial { ArrayList<T>() }

    var numIdle: Int = 0
        get() = 0
    var numActive: Int = 0
        get() = 0

    open fun borrowObject(): T {
        val poolStack = threadPoolStack.get()
        if (poolStack.count() == 0) {
            return factory.create()
        }
        return poolStack.pop()
    }

    open fun returnObject(obj: T) {
        val poolStack = threadPoolStack.get()
        factory.passivateObject(obj)
        poolStack.add(obj)
    }
}
