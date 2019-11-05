package cz.woitee.endlessRunners.geom

/**
 * Created by woitee on 22/01/2017.
 */

data class Vector2Int(var x: Int = 0, var y: Int = 0) {
    operator fun plus(b: Vector2Int): Vector2Int {
        return Vector2Int(x + b.x, y + b.y)
    }

    operator fun minus(b: Vector2Int): Vector2Int {
        return Vector2Int(x - b.x, y - b.y)
    }

    operator fun times(n: Int): Vector2Int {
        return Vector2Int(x * n, y * n)
    }
}
