package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth
import Game.GameObjects.GameObject
import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.IUndoable
import Geom.Direction4
import Geom.Vector2Double
import Geom.direction4
import java.util.*

/**
 * Created by woitee on 15/01/2017.
 */

abstract class MovingObject(x:Double = 0.0, y:Double = 0.0): UndoableUpdateGameObject(x, y) {
    class MovingObjectUndo(val movingObject: MovingObject, val collUndos: List<IUndo>, val x: Double, val y: Double): IUndo {
        override fun undo(gameState: GameState) {
            movingObject.x = x
            movingObject.y = y
            for (collUndo in collUndos.asReversed()) {
                collUndo.undo(gameState)
            }
        }
    }

    override var isUpdated = true
    // speeds are entered in pixels per second
    var xspeed = 0.0
    var yspeed = 0.0

    var velocity: Vector2Double
        get() = Vector2Double(xspeed, yspeed)
        set(value) {xspeed = value.x; yspeed = value.y}

    override fun update(time: Long) {
        gameState.game.collHandler.handleCollisions(this)
        
        this.x += xspeed * time
        this.y += yspeed * time
    }

    override fun undoableUpdate(time: Long): IUndo {
        val undoList = gameState.game.collHandler.handleCollisionsUndoable(this)
        val undo = MovingObjectUndo(this, undoList, this.x, this.y)

        this.x += xspeed * time
        this.y += yspeed * time

        return undo
    }
}