package Geom

import java.util.*

/**
 * Created by woitee on 23/01/2017.
 */

enum class Direction4(val value: Int) {
    NONE(0), UP(1), LEFT(2), DOWN(4), RIGHT(8);

    infix fun or(other: Direction4): Int {
        return value or other.value
    }
}

fun Vector2Int.direction4(): Direction4 {
    return twoNumbers2Direction4(x, y)
}

fun Vector2Double.direction4(): Direction4 {
    return twoNumbers2Direction4(x, y)
}

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