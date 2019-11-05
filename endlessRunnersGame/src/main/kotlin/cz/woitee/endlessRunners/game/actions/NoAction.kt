package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

object NoAction : GameAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        return false
    }

    override fun applyOn(gameState: GameState) {
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return NoUndo
    }

    override fun toString(): String {
        return "NoAction"
    }
}
