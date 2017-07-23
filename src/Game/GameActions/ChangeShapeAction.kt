package Game.GameActions

import Game.GameState
import Game.BlockHeight
import Game.BlockWidth
import Game.GameActions.IGameAction
import Game.GameObjects.SolidBlock
import Game.Undoing.IUndo

/**
 * An action that supports changing shape for the player, e.g. crouching.
 *
 * Created by woitee on 13/01/2017.
 */

class ChangeShape(val targetWidth: Int, val targetHeight: Int): IUndoableHoldAction {
    class ChangeShapeUndo: IUndo {
        override fun undo(gameState: GameState) {
            gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
            gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
        }
    }
    class StopChangeShapeUndo(val targetWidth: Int, val targetHeight: Int): IUndo {
        override fun undo(gameState: GameState) {
            gameState.player.widthBlocks = targetWidth
            gameState.player.widthBlocks = targetHeight
        }
    }
    private val _changeShapeUndo = ChangeShapeUndo()
    private val _stopChangeShapeUndo = StopChangeShapeUndo(targetWidth, targetHeight)

    override fun isApplicableOn(gameState: GameState): Boolean {
        return true
    }
    override fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }

    override fun applyOn(gameState: GameState) {
        gameState.player.widthBlocks = targetWidth
        gameState.player.heightBlocks = targetHeight
    }

    override fun stopApplyingOn(gameState: GameState) {
        gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
        gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        applyOn(gameState)
        return _changeShapeUndo
    }

    override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
        stopApplyingOn(gameState)
        return _stopChangeShapeUndo
    }
}