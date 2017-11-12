package cz.woitee.game.actions

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.HoldAction

/**
 * An action that supports changing shape for the player, e.g. crouching.
 *
 * Created by woitee on 13/01/2017.
 */

class ChangeShapeAction(val targetWidth: Int, val targetHeight: Int, minimumHoldTime: Double = 0.0): HoldAction(minimumHoldTime) {
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
        innerApplyOn(gameState)
        return object: HoldActionUndo(this) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
                gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
            }
        }
    }
    override fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo {
        innerStopApplyingOn(gameState, timeStart)
        return object : HoldActionStopUndo(this, timeStart) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.widthBlocks = targetWidth
                gameState.player.heightBlocks = targetHeight
            }
        }
    }
}