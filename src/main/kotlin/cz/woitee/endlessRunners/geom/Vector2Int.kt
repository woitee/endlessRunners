package cz.woitee.endlessRunners.geom

/**
 * A vector of lengths 2 with Integer values. Provides operators for easy work.
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
