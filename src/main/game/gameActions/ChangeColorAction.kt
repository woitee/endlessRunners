package game.gameActions

import game.GameState
import game.gameActions.abstract.UndoableHoldAction
import game.gameObjects.GameObjectColor

class ChangeColorAction(val targetColor: GameObjectColor): UndoableHoldAction() {
    override fun innerIsApplicableOn(gameState: GameState): Boolean {
        return true
    }

    override fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean {
        return true
    }

    override fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }

    override fun innerApplyOn(gameState: GameState) {
        gameState.player.color = targetColor
    }

    override fun innerStopApplyingOn(gameState: GameState, timeStart: Double) {
        gameState.player.color = gameState.player.defaultColor
    }

    override fun innerApplyUndoablyOn(gameState: GameState): HoldActionUndo {
        val originalColor = gameState.player.color
        innerApplyOn(gameState)
        return object : HoldActionUndo(this) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.color = originalColor
            }
        }
    }

    override fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo {
        innerStopApplyingOn(gameState, timeStart)
        return object : HoldActionStopUndo(this, timeStart) {
            override fun innerUndo(gameState: GameState) {
                gameState.player.color = targetColor
            }
        }
    }
}