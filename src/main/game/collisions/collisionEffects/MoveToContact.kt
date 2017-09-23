package game.collisions.collisionEffects

import game.BlockHeight
import game.BlockWidth
import game.collisions.Collision
import game.objects.GameObject
import game.objects.MovingObject
import game.GameState
import game.undoing.IUndo
import game.undoing.NoActionUndo
import geom.Direction4
import geom.Vector2Double

/**
 * Created by woitee on 23/01/2017.
 */

class MoveToContact: IUndoableCollisionEffect {
    class MoveToContactUndo(val source: MovingObject, val origPos:Vector2Double, val origVel:Vector2Double): IUndo {
        override fun undo(gameState: GameState) {
            source.location = origPos
            source.velocity = origVel
        }
    }

    override fun apply(source: GameObject, collision: Collision) {
        source as MovingObject? ?: return
        val updateTime = source.gameState.lastAdvanceTime

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
        val movingSource = source as MovingObject? ?: return NoActionUndo
        val undo = MoveToContactUndo(source, movingSource.location, movingSource.velocity)
        apply(source, collision)
        return undo
    }
}