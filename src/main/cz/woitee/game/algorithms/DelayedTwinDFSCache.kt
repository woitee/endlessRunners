package cz.woitee.game.algorithms

import cz.woitee.game.Game
import cz.woitee.game.GameState
import java.util.*

class DelayedTwinDFSCache {
    data class TwinCachedState(val currentCached: CachedState, val delayedCached: CachedState) {
        constructor(currentState: GameState, delayedState: GameState): this(CachedState(currentState), CachedState(delayedState))
    }
    val cachedStates = HashSet<TwinCachedState>()

    fun contains(currentState: GameState, delayedState: GameState): Boolean {
        return cachedStates.contains(TwinCachedState(currentState, delayedState))
    }

    fun store(currentState: GameState, delayedState: GameState) {
        cachedStates.add(TwinCachedState(currentState, delayedState))
    }

    fun shouldStore(currentState: GameState, delayedState: GameState): Boolean {
        return true
    }

    fun pruneUnusable(gameState: GameState) {
        for (cachedState in cachedStates.toList()) {
            if (cachedState.delayedCached.playerX < gameState.player.x)
                cachedStates.remove(cachedState)
        }
    }
}