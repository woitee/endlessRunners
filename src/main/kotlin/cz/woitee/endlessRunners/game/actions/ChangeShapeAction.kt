package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An gameAction that supports changing shape for the player, e.g. crouching.
 *
 * Created by woitee on 13/01/2017.
 */

class ChangeShapeAction(val targetWidth: Int, val targetHeight: Int) : HoldButtonAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        applyOn(gameState)
        for (collPoint in gameState.player.collPoints) {
            val gridLoc = gameState.gridLocation(collPoint)

            if (gameState.grid.contains(gridLoc) && gameState.grid[gridLoc]?.isSolid == true) {
                stopApplyingOn(gameState)
                return false
            }
        }
        stopApplyingOn(gameState)
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
        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.widthBlocks = gameState.player.defaultWidthBlocks
                gameState.player.heightBlocks = gameState.player.defaultHeightBlocks
            }
        }
    }
    override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
        stopApplyingOn(gameState)
        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.widthBlocks = targetWidth
                gameState.player.heightBlocks = targetHeight
            }
        }
    }
}
