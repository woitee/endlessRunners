package Geom

/**
 * Created by woitee on 22/01/2017.
 */

data class PointDouble(var x: Double = 0.0, var y: Double = 0.0) {
    fun length(): Double {
        return Math.sqrt(x * x + y * y)
    }

    fun normalize(): PointDouble {
        val len = length()
        x /= len
        y /= len
        return this
    }

    fun normalized(): PointDouble {
        val res = this.copy()
        res.normalize()
        return res
    }
}