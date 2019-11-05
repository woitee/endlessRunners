package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

/**
 * An effect that doesn't do anything.
 */
object NoEffect : UndoableGameEffect() {
    override fun applyOn(gameState: GameState) {
        // do nothing
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return NoUndo
    }

    override fun toString(): String {
        return "NoEffect"
    }
}
