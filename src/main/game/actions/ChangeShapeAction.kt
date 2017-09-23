package game.actions

import game.GameState
import game.actions.abstract.UndoableHoldAction

/**
 * An action that supports changing shape for the player, e.g. crouching.
 *
 * Created by woitee on 13/01/2017.
 */

class ChangeShapeAction(val targetWidth: Int, val targetHeight: Int, minimumHoldTime: Double = 0.0): UndoableHoldAction(minimumHoldTime) {
    override fun innerIsApplicableOn(gameState: GameState): Boolean {
        innerApplyOn(gameState)
        for (collPoint in gameState.player.collPoints) {
            val gridLoc = gameState.gridLocation(collPoint)

            if (gameState.grid.contains(gridLoc) && gameState.grid[gridLoc]?.isSolid == true) {
                innerStopApplyingOn(gameState, 0.0)
                return false
            }
        }
        innerStopApplyingOn(gameState, 0.0)
        return true
    }
    override fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }
    override fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean {
        return true
    }
    override fun innerApplyOn(gameState: GameState) {
        gameState.player.widthBlocks = targetWidth
        gameState.player.heightBlocks = targetHeight
    }

    override fun innerStopApplyingOn(gameState: GameState, timeStart: Double) {
        gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
        gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
    }
    override fun innerApplyUndoablyOn(gameState: GameState): HoldActionUndo {
        applyOn(gameState)
        return object: HoldActionUndo(this) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
                gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
            }
        }
    }
    override fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo {
        stopApplyingOn(gameState)
        return object : HoldActionStopUndo(this, timeStart) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.widthBlocks = targetWidth
                gameState.player.heightBlocks = targetHeight
            }
        }
    }
}