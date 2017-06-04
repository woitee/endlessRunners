package Game.Collisions

import Game.GameObjects.GameObject
import Geom.Direction4
import Geom.Distance2D

/**
 * Created by woitee on 23/01/2017.
 */

data class Collision(
        val other: GameObject,
        val locationX: Double,
        val locationY: Double,
        val myLocationX: Double,
        val myLocationY: Double,
        val direction: Direction4) {
    val distance: Double
        get() = Distance2D.distance(myLocationX, myLocationY, locationX, locationY)
}