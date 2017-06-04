package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.GameObjects.MovingObject
import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.NoActionUndo
import Game.Undoing.UndoFactory
import Geom.Direction4
import Geom.Vector2Double

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
        val updateTime = source.gameState.lastAdvanceTime.toDouble()

        when (collision.direction) {
            Direction4.UP, Direction4.DOWN -> {
                val yDistTravel = source.yspeed * updateTime
                val yDistCollision = collision.locationY - collision.myLocationY
                assert(Math.abs(yDistCollision) < Math.abs(yDistTravel))
                source.y += yDistCollision
                source.yspeed = 0.0
            }
            Direction4.LEFT, Direction4.RIGHT -> {
                val xDistTravel = source.xspeed * updateTime
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