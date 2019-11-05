package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A wrapper for action that tracks times used and undone.
 */
class TrackingAction(val innerAction: GameAction) : GameAction() {
    var timesUsed = 0
    var timesUndone = 0

    override fun isApplicableOn(gameState: GameState): Boolean {
        return innerAction.isApplicableOn(gameState)
    }

    override fun applyOn(gameState: GameState) {
        innerAction.applyOn(gameState)
        ++timesUsed
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = innerAction.applyUndoablyOn(gameState)
        ++timesUsed

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesUsed
                innerUndo.undo(gameState)
                ++timesUndone
            }
        }
    }

    override fun toString(): String {
        return innerAction.toString()
    }
}
