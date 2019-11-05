package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An effect that can happen with a "timeout" - i.e. for a limited amount of time.
 * Start, end, and middle of the effect are represeted by effect object themselves.
 * This can well model well-known powerup features.
 *
 * @param timeout the time after this effect will stop happening
 * @param startEffect The effect that will occur when this starts.
 * @param stopEffect The effect that will occur when this stops.
 * @param runningEffect The effect that will occur in each step this is active.
 */
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

    override fun toString(): String {
        return "TimedEffect(timeout=$timeout, startEffect=$startEffect, stopEffect=$stopEffect, runningEffect=$runningEffect)"
    }
}
