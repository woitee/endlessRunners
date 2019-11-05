package cz.woitee.endlessRunners.geom

/**
 * 4 directional utility class, with helper functions of turning into flags.
 */

enum class Direction4(val value: Int) {
    NONE(0), UP(1), LEFT(2), DOWN(4), RIGHT(8);

    infix fun or(other: Direction4): Int {
        return value or other.value
    }

    companion object {
        private val valMap = Direction4.values().associateBy(Direction4::value)
        fun fromInt(value: Int) = Direction4.valMap[value]!!
        fun any(): Int {
            return 15
        }
        fun directionFlags2String(directionFlags: Int): String {
            val sb = StringBuilder()
            sb.append('[')
            for (dir in values()) {
                if (directionFlags and dir.value != 0) {
                    if (sb.length > 1) sb.append(',')
                    sb.append(dir)
                }
            }
            sb.append(']')
            return sb.toString()
        }
    }
}

fun Vector2Int.direction4(): Direction4 {
    return twoNumbers2Direction4(x, y)
}

fun Vector2Double.direction4(): Direction4 {
    return twoNumbers2Direction4(x, y)
}

/**
 * Converts a vector into the closest direction from 4.
 */
fun twoNumbers2Direction4(x: Int, y: Int): Direction4 {
    return if (x > 0) {
        if (y > x)
            Direction4.UP
        else if (-y > x)
            Direction4.DOWN
        else
            Direction4.RIGHT
    } else if (x < 0) {
        if (y > -x)
            Direction4.UP
        else if (-y > -x)
            Direction4.DOWN
        else
            Direction4.LEFT
    } else {
        if (y > 0)
            Direction4.UP
        else if (y < 0)
            Direction4.DOWN
        else
            Direction4.NONE
    }
}
fun twoNumbers2Direction4(x: Double, y: Double): Direction4 {
    return if (x > 0) {
        if (y > x)
            Direction4.UP
        else if (-y > x)
            Direction4.DOWN
        else
            Direction4.RIGHT
    } else if (x < 0) {
        if (y > -x)
            Direction4.UP
        else if (-y > -x)
            Direction4.DOWN
        else
            Direction4.LEFT
    } else {
        if (y > 0)
            Direction4.UP
        else if (y < 0)
            Direction4.DOWN
        else
            Direction4.NONE
    }
}

fun Int.flagsToDirections(): List<Direction4> {
    return arrayListOf(Direction4.UP, Direction4.LEFT, Direction4.DOWN, Direction4.RIGHT).filter { it -> (it.value and this) != 0 }
}

fun Int.asFlagsContains(dir: Direction4): Boolean {
    return dir.value and this != 0
}

infix fun Int.or(dir: Direction4): Int {
    return this.or(dir)
}
