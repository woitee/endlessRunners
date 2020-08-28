package cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.algorithms.dfs.CachedState
import cz.woitee.endlessRunners.utils.MySerializable
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*

/**
 * The delayed twin dfs algorithm requires a special cache - caching positions of both of its states simultaneously.
 */
class DelayedTwinDFSCache : MySerializable {
    data class TwinCachedState(val currentCached: CachedState, val delayedCached: CachedState) : Serializable {
        constructor(currentState: GameState, delayedState: GameState) : this(CachedState(currentState), CachedState(delayedState))
        // Button model does not need to worry which gameAction are used in which states, merely the buttons pressed
        constructor(buttonModel: ButtonModel) : this(buttonModel.currentState, buttonModel.delayedState) {
            currentCached.heldActionFlags = buttonModel.heldButtonsAsFlags()
            delayedCached.heldActionFlags = currentCached.heldActionFlags
        }
    }

    /**
     * A mapping of TwinCachedState -> gameTime when this was added
     */
    val cachedStates = HashMap<TwinCachedState, Double>()

    fun contains(currentState: GameState, delayedState: GameState): Boolean {
        return cachedStates.containsKey(TwinCachedState(currentState, delayedState))
    }
    fun contains(buttonModel: ButtonModel): Boolean {
        return cachedStates.containsKey(TwinCachedState(buttonModel))
    }

    fun store(currentState: GameState, delayedState: GameState, currentGameTime: Double) {
        cachedStates[TwinCachedState(currentState, delayedState)] = currentGameTime
    }
    fun store(buttonModel: ButtonModel, currentGameTime: Double) {
        cachedStates[TwinCachedState(buttonModel)] = currentGameTime
    }

    fun shouldStore(currentState: GameState, delayedState: GameState): Boolean {
        return true
    }
    fun shouldStore(buttonModel: ButtonModel): Boolean {
        return true
    }

    fun clear() {
        cachedStates.clear()
    }

    fun count(): Int {
        return cachedStates.count()
    }

    fun clearAddedSince(gameTime: Double) {
        cachedStates
            .filter { it.value >= gameTime }
            .forEach { cachedStates.remove(it.key) }
    }

    fun pruneUnusable(gameState: GameState) {
        cachedStates
            .filterKeys { it.delayedCached.playerX < gameState.player.x }
            .forEach { cachedStates.remove(it.key) }
    }

    override fun writeObject(oos: ObjectOutputStream): DelayedTwinDFSCache {
        oos.writeInt(cachedStates.size)
        for (state in cachedStates) {
            oos.writeObject(oos)
        }
        return this
    }

    override fun readObject(ois: ObjectInputStream): DelayedTwinDFSCache {
        cachedStates.clear()
        val size = ois.readInt()
        for (i in 0 until size) {
            cachedStates[ois.readObject() as TwinCachedState] = 0.0
        }
        return this
    }
}
