package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A wrapper for an effect that tracks times applied and undone.
 */
class TrackingEffect(val innerEffect: UndoableGameEffect) : UndoableGameEffect() {
    var timesApplied = 0
    var timesUndone = 0

    override fun applyOn(gameState: GameState) {
        innerEffect.applyOn(gameState)
        ++timesApplied
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = innerEffect.applyUndoablyOn(gameState)
        ++timesApplied

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesApplied
                innerUndo.undo(gameState)
                ++timesUndone
            }
        }
    }

    override fun toString(): String {
        return innerEffect.toString()
    }
}
