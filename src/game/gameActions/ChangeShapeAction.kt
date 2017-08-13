package game.gameActions

import game.BlockHeight
import game.BlockWidth
import game.GameState
import game.gameActions.abstract.UndoableHoldAction

/**
 * An action that supports changing shape for the player, e.g. crouching.
 *
 * Created by woitee on 13/01/2017.
 */

class ChangeShapeAction(val targetWidth: Int, val targetHeight: Int): UndoableHoldAction() {
    class ChangeShapeUndo(holdAction: ChangeShapeAction): HoldActionUndo(holdAction) {
        override fun innerUndo(gameState: GameState) {
            gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
            gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
        }
    }
    class StopChangeShapeUndo(
            holdAction: ChangeShapeAction,
            val timeStart: Double): HoldActionStopUndo(holdAction, timeStart) {
        override fun innerUndo(gameState: GameState) {
            gameState.player.widthBlocks = (holdAction as ChangeShapeAction).targetWidth
            gameState.player.heightBlocks = holdAction.targetHeight
        }
    }
    private val _changeShapeUndo = ChangeShapeUndo(this)

    override fun innerIsApplicableOn(gameState: GameState): Boolean {
        for (x in 0 .. targetWidth) {
            for (y in 0 .. targetHeight) {
                val gridX = (gameState.player.x / BlockWidth).toInt() - gameState.gridX + x
                val gridY = ((gameState.player.y - 1) / BlockHeight).toInt() + y
                if (gridX >= gameState.grid.width || gridY >= gameState.grid.height) {
                    return true
                }
                if (gameState.grid[gridX, gridY]?.isSolid == true) {
                    return false
                }
            }
        }
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
        return _changeShapeUndo
    }
    override fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo {
        stopApplyingOn(gameState)
        return StopChangeShapeUndo(this, timeStart)
    }
}