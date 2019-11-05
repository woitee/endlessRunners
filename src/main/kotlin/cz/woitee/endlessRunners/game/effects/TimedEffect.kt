package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

open class TimedEffect(
    val timeout: Double,
    val startEffect: UndoableGameEffect,
    val stopEffect: UndoableGameEffect,
    val runningEffect: UndoableGameEffect = NoEffect
) : UndoableGameEffect() {

    override fun applyOn(gameState: GameState) {
        startEffect.applyOn(gameState)

        gameState.timedEffects[gameState.gameTime + timeout] = this
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val innerUndo = startEffect.applyUndoablyOn(gameState)

        return object : IUndo {
            override fun undo(gameState: GameState) {
                if (timeout > 0.0) {
                    gameState.timedEffects.remove(gameState.gameTime + timeout)
                }
                innerUndo.undo(gameState)
            }
        }
    }
}
