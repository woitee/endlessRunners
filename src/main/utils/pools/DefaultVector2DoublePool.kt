package utils.pools

import geom.Vector2Double

/**
 * Created by woitee on 04/06/2017.
 */
object DefaultVector2DoublePool: SimplePool<Vector2Double>(DefaultVector2DoubleFactory()) {
    class DefaultVector2DoubleFactory: SimpleFactory<Vector2Double>() {
        override fun create(): Vector2Double {
            println("Creating object")
            return Vector2Double()
        }
    }

    fun borrowCoords(x: Double, y: Double): Vector2Double {
        val vec = borrowObject()
        vec.x = x
        vec.y = y
        return vec
    }

    override fun returnObject(obj: Vector2Double) {
        println("Returning object")
        super.returnObject(obj)
    }
}