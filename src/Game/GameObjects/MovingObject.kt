package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth
import Game.GameState
import Game.Undoing.IUndo
import Geom.Vector2Double

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
    // speeds are entered in blocks per second
    var xspeed = 0.0
    var yspeed = 0.0

    var velocity: Vector2Double
        get() = Vector2Double(xspeed, yspeed)
        set(value) {xspeed = value.x; yspeed = value.y}

    override fun update(time: Double) {
        gameState.game.collHandler.handleCollisions(this)

        updateMovement(time)
    }

    override fun undoableUpdate(time: Double): IUndo {
        val undoList = gameState.game.collHandler.handleCollisionsUndoable(this)
        val undo = MovingObjectUndo(this, undoList, this.x, this.y)

        updateMovement(time)

        return undo
    }

    private fun updateMovement(time: Double) {
        this.x += xspeed * time * BlockWidth
        this.y += yspeed * time * BlockHeight
    }
}