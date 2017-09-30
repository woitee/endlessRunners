package cz.woitee.geom

/**
 * Created by woitee on 05/06/2017.
 */
object Distance2D {
    fun distance(ax: Double, ay: Double, bx: Double, by: Double): Double {
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by))
    }
}