package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An action that just applies a GameEffect.
 */
open class ApplyGameEffectAction(var gameEffect: UndoableGameEffect) : GameAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        return true
    }

    override fun applyOn(gameState: GameState) {
        gameEffect.applyOn(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return gameEffect.applyUndoablyOn(gameState)
    }

    override fun toString(): String {
        return "ApplyGameEffect($gameEffect)"
    }
}
