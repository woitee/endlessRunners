package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo
import cz.woitee.endlessRunners.geom.Direction4

/**
 * Basic CollisionEffect for not moving through platforms - the object moves only until contact.
 */

class MoveToContact : IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        source as MovingObject? ?: return
        val updateTime = source.gameState.lastAdvanceTime
        if (source is Player) { source.timesJumpedSinceTouchingGround = 0 }

        when (collision.direction) {
            Direction4.UP, Direction4.DOWN -> {
                val yDistTravel = source.yspeed * updateTime * BlockHeight
                val yDistCollision = collision.locationY - collision.myLocationY
                assert(Math.abs(yDistCollision) < Math.abs(yDistTravel))
                source.y += yDistCollision
                source.yspeed = 0.0
            }
            Direction4.LEFT, Direction4.RIGHT -> {
                val xDistTravel = source.xspeed * updateTime * BlockWidth
                val xDistCollision = collision.locationX - collision.myLocationX
                assert(Math.abs(xDistCollision) < Math.abs(xDistTravel))
                source.x += xDistCollision
                source.xspeed = 0.0
            }
            Direction4.NONE -> {}
        }
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        val movingSource = source as MovingObject? ?: return NoUndo
        val currentLocation = movingSource.location
        val currentVelocity = movingSource.velocity
        val currentJumpCount = (movingSource as? Player)?.timesJumpedSinceTouchingGround ?: 0

        val undo = object : IUndo {
            override fun undo(gameState: GameState) {
                movingSource.location = currentLocation
                movingSource.velocity = currentVelocity
                (movingSource as? Player)?.timesJumpedSinceTouchingGround = currentJumpCount
            }
        }
        apply(source, collision)
        return undo
    }

    override fun toString(): String {
        return "MoveToContact"
    }
}
