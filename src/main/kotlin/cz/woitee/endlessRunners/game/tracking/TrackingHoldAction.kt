package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A wrapper for a hold action that tracks times started, held, released, and undone.
 */
class TrackingHoldAction(val innerAction: HoldButtonAction) : HoldButtonAction() {
    var timesStarted = 0
    var timesUndoneStart = 0
    var timesStopped = 0
    var timesUndoneStop = 0
    var timesKeptHeld = 0
    var timesUndoneKeepHolding = 0

    override fun isApplicableOn(gameState: GameState): Boolean {
        return innerAction.isApplicableOn(gameState)
    }

    override fun applyOn(gameState: GameState) {
        innerAction.applyOn(gameState)
        ++timesStarted
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = innerAction.applyUndoablyOn(gameState)
        ++timesStarted

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesStarted
                innerUndo.undo(gameState)
                ++timesUndoneStart
            }
        }
    }

    override fun stopApplyingOn(gameState: GameState) {
        innerAction.stopApplyingOn(gameState)
        ++timesStopped
    }
    override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = innerAction.stopApplyingUndoablyOn(gameState)
        ++timesStopped

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesStopped
                innerUndo.undo(gameState)
                ++timesUndoneStop
            }
        }
    }

    override fun keepApplyingOn(gameState: GameState) {
        innerAction.keepApplyingOn(gameState)
        ++timesKeptHeld
    }
    override fun keepApplyingUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = innerAction.keepApplyingUndoablyOn(gameState)
        ++timesKeptHeld

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesKeptHeld
                innerUndo.undo(gameState)
                ++timesUndoneKeepHolding
            }
        }
    }
    override fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return innerAction.canBeKeptApplyingOn(gameState)
    }
    override fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return innerAction.canBeStoppedApplyingOn(gameState)
    }

    override fun toString(): String {
        return innerAction.toString()
    }
}
