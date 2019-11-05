package cz.woitee.endlessRunners.game.collisions

import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.geom.Direction4
import cz.woitee.endlessRunners.geom.Distance2D

/**
 * An object representing details of a GameObject colliding with another.
 *
 * @param other The other object of the collision.
 * @param locationX The x coordinate of where the collision occured in a GameState.
 * @param locationY The y coordinate of where the collision occured in a GameState.
 * @param myLocationX The x coordinate on my body (relative to me), where the collision occurs.
 * @param myLocationY The y coordinate on my body (relative to me), where the collision occurs.
 * @param direction The direction in which the collision happened.
 */
data class Collision(
    val other: GameObject,
    val locationX: Double,
    val locationY: Double,
    val myLocationX: Double,
    val myLocationY: Double,
    val direction: Direction4
) {

    constructor (other: GameObject, locationX: Double, locationY: Double, myLocationX: Double, myLocationY: Double) :
            this (other, locationX, locationY, myLocationX, myLocationY, CollisionUtils.gameObjectLocToDir(other, locationX, locationY))

    val distance: Double
        get() = Distance2D.distance(myLocationX, myLocationY, locationX, locationY)
}
