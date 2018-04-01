package cz.woitee.utils.pools

import cz.woitee.utils.pop
import java.util.*

/**
 * Created by woitee on 04/06/2017.
 */
open class SimplePool<T> (val factory: SimpleFactory<T>) {
    val poolStack = ArrayList<T>()

    var numIdle: Int = 0
        get() = 0
    var numActive: Int = 0
        get() = 0

    open fun borrowObject(): T {
        if (poolStack.count() == 0) {
            return factory.create()
        }
        return poolStack.pop()
    }

    open fun returnObject(obj: T) {
        factory.passivateObject(obj)
        poolStack.add(obj)
    }
}