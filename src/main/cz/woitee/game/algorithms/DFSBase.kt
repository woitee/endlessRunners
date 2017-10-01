package cz.woitee.game.algorithms

import cz.woitee.gui.GamePanelVisualizer
import cz.woitee.game.BlockWidth
import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.actions.abstract.UndoableHoldAction
import cz.woitee.game.undoing.UndoFactory
import cz.woitee.utils.pop
import java.util.*

/**
 * A place that you can start when creating a DFS or some other search algorithm for the game. Provides useful methods,
 * measures time running, etc.
 *
 * Created by woitee on 30/04/2017.
 */

abstract class DFSBase (val persistentCache:Boolean = true, var maxDepth: Int = 1000, var debug: Boolean = false) {
    data class SearchResult(val success: Boolean, val action: GameAction? = null)
    /**
     * Assistant class for notifying that the last state advance managed to only perform the action, and not a state update.
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
    protected var maxX: Int = 0
    protected var updateTime: Double = 0.0
    protected val cachedStates = HashSet<CachedState>()
    /**
     * Searches for an action that doesn't lead to death and returns it, or null if it doesn't exist.
     */
    fun searchForAction (gameState: GameState, updateTime: Double = -1.0): GameAction? {
        this.updateTime = if (updateTime < 0) gameState.game.updateTime else updateTime
        if (persistentCache) {
            pruneUnusableCache(gameState)
        } else {
            cachedStates.clear()
        }

        lastStats = SearchStats()
        val startTime = System.nanoTime()
        val visualizer = gameState.game.visualizer as GamePanelVisualizer?
        if (debug)
            visualizer?.debugObjects?.clear()

        maxX = (gameState.gridX + gameState.grid.width) * BlockWidth
        val result = if (debug) {
            searchInternal(gameState, this.updateTime)
        } else {
            synchronized(gameState.gameObjects) {
                searchInternal(gameState, this.updateTime)
            }
        }
        lastStats.success = result.success
        lastStats.cachedStates = cachedStates.count()
        lastStats.timeTaken = (System.nanoTime() - startTime).toDouble() / 1000000000
        return result.action
    }

    abstract protected fun searchInternal(gameState: GameState, updateTime: Double): SearchResult

    open fun reset() {
        cachedStates.clear()
    }

    /**
     * Returns actions that should be tried ordered by priority, which is:
     * a) stop holding an action
     * b) do nothing (null)
     * c) do a non-holding action
     * d) start holding an action
     *
     * Note that there is always some actionIx in this list, as it also contains null.
     */
    open protected fun orderedPerformableActions(gameState: GameState): List<UndoableAction?> {
        val list = ArrayList<UndoableAction?>()
        // stop holding action
        for (heldAction in gameState.heldActions.keys) {
            if (heldAction.canBeStoppedApplyingOn(gameState))
                list.add((heldAction as UndoableHoldAction).asStopAction)
        }
        // do nothing
        list.add(null)
        // do a non-holding action
        for (action in gameState.allActions) {
            if (action !is UndoableHoldAction && action.isApplicableOn(gameState))
                list.add(action as UndoableAction)

        }
        // start holding an action
        for (action in gameState.allActions) {
            if (action is UndoableHoldAction && action.isApplicableOn(gameState))
                list.add(action.asStartAction)
        }
        return list
    }

    /**
     * Decides whether the current gameState should be cached to not be searched multiple times from.
     */
    open protected fun shouldCache(gameState: GameState): Boolean {
//        return gameState.grid[
//            gameState.gridLocation(
//                gameState.player.x - gameState.gridX * BlockWidth,
//                gameState.player.y - 0.1
//            )
//        ]?.isSolid == true
        return true
    }
    protected fun cache(gameState: GameState) {
        cachedStates.add(CachedState(gameState))
    }
    protected fun isInCache(gameState: GameState): Boolean {
        return cachedStates.contains(CachedState(gameState))
    }
    open protected fun pruneUnusableCache(gameState: GameState) {
        for (cachedState in cachedStates.toList()) {
            if (cachedState.playerX < gameState.player.x)
                cachedStates.remove(cachedState)
        }
    }

    protected fun advanceState(gameState: GameState, gameAction: UndoableAction?): IUndo {
        ++lastStats.searchedStates
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else {
            val firstUndo = gameAction.applyUndoablyOn(gameState)
            if (gameState.player.nextX(updateTime) + gameState.player.widthPx >= maxX) {
                // This only happens as the last update at the end of the screen, and generally is successful,
                // so really happens at MOST once per search. The extra class encapsulation overhead is negligible
                return NoStateAdvanceUndo(firstUndo)
            }
            val secondUndo = gameState.advanceUndoable(updateTime)
            return UndoFactory.doubleUndo(firstUndo, secondUndo)
        }
    }
}