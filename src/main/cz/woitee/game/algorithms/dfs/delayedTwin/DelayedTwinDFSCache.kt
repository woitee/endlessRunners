package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.CachedState
import cz.woitee.utils.MySerializable
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.io.Serializable

class DelayedTwinDFSCache: MySerializable {
    data class TwinCachedState(val currentCached: CachedState, val delayedCached: CachedState): Serializable {
        constructor(currentState: GameState, delayedState: GameState) : this(CachedState(currentState), CachedState(delayedState))
        // Button model does not need to worry which gameAction are used in which states, merely the buttons pressed
        constructor(buttonModel: ButtonModel) : this(buttonModel.currentState, buttonModel.delayedState) {
            currentCached.heldActionFlags = buttonModel.heldButtonsAsFlags()
            delayedCached.heldActionFlags = currentCached.heldActionFlags
        }
    }
    val cachedStates = HashSet<TwinCachedState>()

    fun contains(currentState: GameState, delayedState: GameState): Boolean {
        return cachedStates.contains(TwinCachedState(currentState, delayedState))
    }
    fun contains(buttonModel: ButtonModel): Boolean {
        return cachedStates.contains(TwinCachedState(buttonModel))
    }

    fun store(currentState: GameState, delayedState: GameState) {
        cachedStates.add(TwinCachedState(currentState, delayedState))
    }
    fun store(buttonModel: ButtonModel) {
        cachedStates.add(TwinCachedState(buttonModel))
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

    fun pruneUnusable(gameState: GameState) {
        for (cachedState in cachedStates.toList()) {
            if (cachedState.delayedCached.playerX < gameState.player.x)
                cachedStates.remove(cachedState)
        }
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
            cachedStates.add(ois.readObject() as TwinCachedState)
        }
        return this
    }
}