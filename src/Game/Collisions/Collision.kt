package Game.Collisions

import Game.GameObjects.GameObject
import Geom.Direction4
import Geom.Distance2D

/**
 * Created by woitee on 23/01/2017.
 */

private fun gameObjectLocToDir(gameObject: GameObject, locationX: Double, locationY: Double): Direction4 {
    if (apxEquals(gameObject.x, locationX)) {
        return Direction4.RIGHT
    } else if (apxEquals(gameObject.y, locationY)) {
        return Direction4.UP
    } else if (apxEquals(gameObject.y + gameObject.heightPx, locationY)) {
        return Direction4.DOWN
    } else if (apxEquals(gameObject.x + gameObject.widthPx, locationX)) {
        return Direction4.LEFT
    }
    return Direction4.NONE
}

private fun apxEquals(a: Double, b: Double, epsilon: Double = 0.0001): Boolean {
    return a == b || Math.abs(a - b) < epsilon
}

data class Collision(
        val other: GameObject,
        val locationX: Double,
        val locationY: Double,
        val myLocationX: Double,
        val myLocationY: Double,
        val direction: Direction4) {

    constructor (other: GameObject, locationX: Double, locationY: Double, myLocationX: Double, myLocationY: Double):
            this (other, locationX, locationY, myLocationX, myLocationY, gameObjectLocToDir(other, locationX, locationY))


    val distance: Double
        get() = Distance2D.distance(myLocationX, myLocationY, locationX, locationY)
}