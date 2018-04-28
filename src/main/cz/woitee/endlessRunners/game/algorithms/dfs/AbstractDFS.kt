package cz.woitee.endlessRunners.game.algorithms.dfs

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import java.util.*

/**
 * A place that you can init when creating a BasicDFS or some other search algorithm for the game. Provides useful methods,
 * measures time running, etc.
 *
 * Created by woitee on 30/04/2017.
 */

abstract class AbstractDFS(val persistentCache:Boolean = true, var maxDepth: Int = 1000, var debug: Boolean = false) {
    data class SearchResult(val success: Boolean, val action: GameButton.StateChange? = null)
    /**
     * Assistant class for notifying that the last state advance managed to only perform the gameAction, and not a state update.
     */
    class NoStateAdvanceUndo(val undo: IUndo): IUndo {
        override fun undo(gameState: GameState) {
            undo.undo(gameState)
        }
    }

    /**
     * Holding statistics from the last search.
     */
    var lastStats = SearchStats()
    protected var updateTime: Double = 0.0
    protected val cachedStates = HashSet<CachedState>()
    open val currentlyCachedStates: Int
        get() = cachedStates.count()

    /**
     * Searches for an gameAction that doesn't lead to death and returns it, or null if it doesn't exist.
     */
    fun searchForAction (gameState: GameState, updateTime: Double = -1.0): GameButton.StateChange? {
        this.updateTime = if (updateTime < 0) gameState.game.updateTime else updateTime
        if (persistentCache) {
            pruneUnusableCache(gameState)
        } else {
            clearCache()
        }

        lastStats = SearchStats()
        val startTime = System.nanoTime()
        val visualizer = gameState.game.visualizer as GamePanelVisualizer?
        if (debug)
            visualizer?.debugObjects?.clear()

        val result = if (debug) {
            searchInternal(gameState, this.updateTime)
        } else {
            synchronized(gameState.gameObjects) {
                searchInternal(gameState, this.updateTime)
            }
        }
        lastStats.success = result.success
        lastStats.cachedStates = currentlyCachedStates
        lastStats.timeTaken = (System.nanoTime() - startTime).toDouble() / 1000000000
        return result.action
    }

    abstract protected fun searchInternal(gameState: GameState, updateTime: Double): SearchResult

    open fun init(gameState: GameState) {
        updateTime = gameState.game.updateTime
        clearCache()
    }

    protected fun isPlayerAtEnd(gameState: GameState): Boolean {
        return gameState.player.nextX(this.updateTime) + gameState.player.widthPx >= gameState.maxX
    }

    /**
     * Returns actions that should be tried ordered by priority, which is:
     * a) stop holding a gameAction
     * b) do nothing (null)
     * c) do a non-holding gameAction
     * d) start holding a gameAction
     *
     * Note that there is always some actionIx in this list, as it also contains null.
     */
    open fun orderedPerformableButtonActions(gameState: GameState): List<GameButton.StateChange?> {
        val list = ArrayList<GameButton.StateChange?>()
        // dispose holding gameAction
        for (button in gameState.buttons) {
            if (button.isPressed && (button.action !is HoldButtonAction || button.action.canBeStoppedApplyingOn(gameState)))
                list.add(button.release)
        }
        // do nothing (we can always do nothing)
        list.add(null)
        // do a non-holding gameAction
        for (button in gameState.buttons) {
            if (!button.isPressed && button.action !is HoldButtonAction && button.action.isApplicableOn(gameState))
                list.add(button.hold) // DFS always holds
        }
        // start holding a holdaction
        for (button in gameState.buttons) {
            if (!button.isPressed && button.action is HoldButtonAction && button.action.isApplicableOn(gameState))
                list.add(button.hold)
        }
        return list
    }

    /**
     * Decides whether the current currentState should be cached to not be searched multiple times from.
     */
    protected open fun shouldCache(gameState: GameState): Boolean {
        return true
    }
    protected fun cache(gameState: GameState) {
        cachedStates.add(CachedState(gameState))
    }
    protected fun isInCache(gameState: GameState): Boolean {
        return cachedStates.contains(CachedState(gameState))
    }
    protected open fun pruneUnusableCache(gameState: GameState) {
        cachedStates
                .filter { it.playerX < gameState.player.x }
                .forEach { cachedStates.remove(it) }
    }
    protected open fun clearCache() {
        cachedStates.clear()
    }

    protected fun advanceState(gameState: GameState, buttonStateChange: GameButton.StateChange?): IUndo {
        ++lastStats.searchedStates
        return gameState.advanceUndoableByAction(buttonStateChange, updateTime)
    }


    /**
     * This function is called whenever the state we are helping to simulate changes. It can be useful to update
     * internal structures based on it.
     */
    open internal fun onUpdate(updateTime: Double, buttonStateChange: GameButton.StateChange?, gameState: GameState) {
    }
}