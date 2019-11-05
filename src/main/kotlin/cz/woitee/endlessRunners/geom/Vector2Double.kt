package cz.woitee.endlessRunners.geom

import java.io.Serializable

/**
 * Created by woitee on 22/01/2017.
 */

data class Vector2Double(var x: Double = 0.0, var y: Double = 0.0) : Serializable {
    fun length(): Double {
        return Math.sqrt(x * x + y * y)
    }

    fun normalize(): Vector2Double {
        val len = length()
        x /= len
        y /= len
        return this
    }

    fun normalized(): Vector2Double {
        val res = this.copy()
        res.normalize()
        return res
    }

    fun distanceFrom(p: Vector2Double): Double {
        return distanceFrom(p.x, p.y)
    }
    fun distanceFrom(ax: Double, ay: Double): Double {
        return Distance2D.distance(x, y, ax, ay)
    }

    operator fun plus(b: Vector2Double): Vector2Double {
        return Vector2Double(x + b.x, y + b.y)
    }

    operator fun minus(b: Vector2Double): Vector2Double {
        return Vector2Double(x - b.x, y - b.y)
    }

    operator fun times(r: Double): Vector2Double {
        return Vector2Double(x * r, y * r)
    }
}
