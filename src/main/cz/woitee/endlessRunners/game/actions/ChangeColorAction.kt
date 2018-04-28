package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.objects.GameObjectColor
import cz.woitee.endlessRunners.game.undoing.IUndo

class ChangeColorAction(val targetColor: GameObjectColor): HoldButtonAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        return true
    }

    override fun applyOn(gameState: GameState) {
        gameState.player.color = targetColor
    }

    override fun stopApplyingOn(gameState: GameState) {
        gameState.player.color = gameState.player.defaultColor
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val originalColor = gameState.player.color
        applyOn(gameState)

        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.color = originalColor
            }
        }
    }

    override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
        val originalColor = gameState.player.color
        stopApplyingOn(gameState)

        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.color = originalColor
            }
        }
    }
}