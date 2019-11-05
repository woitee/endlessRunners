package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

object NoEffect: UndoableGameEffect() {
    override fun applyOn(gameState: GameState) {
        // do nothing
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return NoUndo
    }
}