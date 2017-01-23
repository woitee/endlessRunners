package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.GameObjects.MovingObject
import Game.GameState
import Geom.Direction4

/**
 * Created by woitee on 23/01/2017.
 */

class MoveToContact(): CollisionEffect() {
    override fun apply(source: GameObject, collision: Collision) {
        val movingSource = source as MovingObject? ?: return
        val updateTime = source.gameState.lastAdvanceTime.toDouble()

        when (collision.direction) {
            Direction4.UP, Direction4.DOWN -> {
                val yDistTravel = source.yspeed * updateTime
                val yDistCollision = collision.distance
                assert(Math.abs(yDistCollision) < Math.abs(yDistTravel))
                source.yspeed *= yDistCollision / yDistTravel
            }
            Direction4.LEFT, Direction4.RIGHT -> {
                val xDistTravel = source.xspeed * updateTime
                val xDistCollision = collision.distance
                assert(Math.abs(xDistCollision) < Math.abs(xDistTravel))
                source.yspeed *= xDistCollision / xDistTravel
            }
            Direction4.NONE -> {}
        }
    }
}