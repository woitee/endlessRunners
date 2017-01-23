package Game.Collisions

import Game.GameObjects.GameObject
import Geom.Direction4
import Geom.Vector2Double

/**
 * Created by woitee on 23/01/2017.
 */

data class Collision(val other: GameObject, val location: Vector2Double, val myLocation: Vector2Double, val direction: Direction4) {
    val distance: Double
        get() = myLocation.distanceFrom(location)
}