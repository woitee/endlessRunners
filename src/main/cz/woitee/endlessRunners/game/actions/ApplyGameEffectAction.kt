package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.undoing.IUndo

open class ApplyGameEffectAction(val gameEffect: UndoableGameEffect): GameButtonAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        return true
    }

    override fun applyOn(gameState: GameState) {
        gameEffect.applyOn(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return gameEffect.applyUndoablyOn(gameState)
    }
}