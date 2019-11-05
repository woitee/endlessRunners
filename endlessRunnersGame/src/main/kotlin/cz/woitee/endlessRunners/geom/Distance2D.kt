package cz.woitee.endlessRunners.geom

/**
 * Calculation of distance in 2D space.
 */
object Distance2D {
    /**
     * Gets the distance between two points.
     */
    fun distance(ax: Double, ay: Double, bx: Double, by: Double): Double {
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by))
    }
}
