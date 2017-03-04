package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.GameObjects.MovingObject
import Game.GameState
import Geom.Direction4

/**
 * Created by woitee on 23/01/2017.
 */

class MoveToContact: CollisionEffect() {
    override fun apply(source: GameObject, collision: Collision) {
        val movingSource = source as MovingObject? ?: return
        val updateTime = source.gameState.lastAdvanceTime.toDouble()

        when (collision.direction) {
            Direction4.UP, Direction4.DOWN -> {
                val yDistTravel = source.yspeed * updateTime
                val yDistCollision = collision.location.y - collision.myLocation.y
                assert(Math.abs(yDistCollision) < Math.abs(yDistTravel))
                source.y += yDistCollision
                source.yspeed = 0.0
            }
            Direction4.LEFT, Direction4.RIGHT -> {
                val xDistTravel = source.xspeed * updateTime
                val xDistCollision = collision.location.x - collision.myLocation.x
                assert(Math.abs(xDistCollision) < Math.abs(xDistTravel))
                source.x += xDistCollision
                source.xspeed = 0.0
            }
            Direction4.NONE -> {}
        }
    }
}