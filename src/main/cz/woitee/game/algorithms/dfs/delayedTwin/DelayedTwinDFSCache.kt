package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.CachedState
import java.util.*

class DelayedTwinDFSCache {
    data class TwinCachedState(val currentCached: CachedState, val delayedCached: CachedState) {
        constructor(currentState: GameState, delayedState: GameState) : this(CachedState(currentState), CachedState(delayedState))
        constructor(buttonModel: ButtonModel) : this(buttonModel.currentState, buttonModel.delayedState)
    }
    val cachedStates = HashSet<TwinCachedState>()

    fun contains(currentState: GameState, delayedState: GameState): Boolean {
        return cachedStates.contains(TwinCachedState(currentState, delayedState))
    }
    fun contains(buttonModel: ButtonModel): Boolean {
        return contains(buttonModel.currentState, buttonModel.delayedState)
    }

    fun store(currentState: GameState, delayedState: GameState) {
        cachedStates.add(TwinCachedState(currentState, delayedState))
    }
    fun store(buttonModel: ButtonModel) {
        store(buttonModel.currentState, buttonModel.delayedState)
    }

    fun shouldStore(currentState: GameState, delayedState: GameState): Boolean {
        return true
    }
    fun shouldStore(buttonModel: ButtonModel): Boolean {
        return shouldStore(buttonModel.currentState, buttonModel.delayedState)
    }

    fun pruneUnusable(gameState: GameState) {
        for (cachedState in cachedStates.toList()) {
            if (cachedState.delayedCached.playerX < gameState.player.x)
                cachedStates.remove(cachedState)
        }
    }
}