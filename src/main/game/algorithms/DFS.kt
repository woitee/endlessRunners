package game.algorithms

import gui.GamePanelVisualizer
import game.BlockWidth
import game.GameState
import game.undoing.IUndo
import game.actions.abstract.GameAction
import game.actions.abstract.UndoableAction
import game.actions.abstract.UndoableHoldAction
import game.undoing.UndoFactory
import utils.pop
import java.util.*

/**
 * Created by woitee on 30/04/2017.
 */

class DFS (val persistentCache:Boolean = true) {
    data class CachedState(val playerX: Double, val playerY: Double, val playerYSpeed: Double) {
        constructor (gameState: GameState): this(gameState.player.x, gameState.player.y, gameState.player.yspeed)
    }
    /**
     * This class contains a couple members, that hold stats from last search.
     */
    var lastStats = SearchStats()
    private var maxX: Int = 0
    private var updateTime: Double = 0.0
    private val cachedStates = HashSet<CachedState>()
    /**
     * Searches for an action that doesn't lead to death and returns it, or null if it doesn't exist.
     */
    fun searchForAction (gameState: GameState, maxDepth: Int = 1000, updateTime: Double = -1.0, debug:Boolean = false): GameAction? {
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

        synchronized(gameState.gameObjects) {
            val undoList = ArrayList<IUndo>()
            val actionList = ArrayList<Int>()
            val possibleActionsList = ArrayList<List<UndoableAction?>>()

            maxX = (gameState.gridX + gameState.grid.width) * BlockWidth
            while (undoList.count() < maxDepth && gameState.player.nextX(this.updateTime) + gameState.player.widthPx < maxX) {
                val currentActions: List<UndoableAction?> = orderedPerformableActions(gameState)
                undoList.add(advanceState(gameState, currentActions[0], this.updateTime))

                if (undoList.count() > lastStats.reachedDepth)
                    lastStats.reachedDepth = undoList.count()
                actionList.add(0)
                possibleActionsList.add(currentActions)
                if (gameState.isGameOver || isInCache(gameState)) {
                    //backtrack
                    var finishedBacktrack = false
                    while (!finishedBacktrack) {
                        if (undoList.isEmpty()) {
                            lastStats.timeTaken = ((System.nanoTime() - startTime)/1000).toDouble() / 1000000
                            // No option but to lose the game
                            return null
                        }
                        undoList.pop().undo(gameState)
                        ++lastStats.backtrackedStates
                        var action = actionList.pop() + 1
                        val actions = possibleActionsList.pop()
                        while (action < actions.count()) {
                            val undo = advanceState(gameState, actions[action], this.updateTime)
                            if (gameState.isGameOver || isInCache(gameState)) {
                                undo.undo(gameState)
                                ++lastStats.backtrackedStates
                                ++action
                            } else {
                                undoList.add(undo)
                                actionList.add(action)
                                possibleActionsList.add(actions)
                                finishedBacktrack = true
                                break
                            }
                        }
                        if (action >= actions.count())
                            if (shouldCache(gameState)) cache(gameState)
                    }
                }
            }

            for (undo in undoList.asReversed()) {
                undo.undo(gameState)
            }

            val actionIx = actionList[0]
            val action = possibleActionsList[0][actionIx]
            lastStats.success = true
            lastStats.cachedStates = cachedStates.count()
//            for (cachedState in cachedStates) {
//                if (debug && count++ % 15 == 0)
//                    visualizer?.debugObjects?.add(Player(cachedState.x, cachedState.y))
//            }
            lastStats.timeTaken = (System.nanoTime() - startTime).toDouble() / 1000000000
            return action
        }
    }

    fun reset() {
        cachedStates.clear()
    }

    /**
     * Returns actions that should be tried ordered by priority, which is:
     * a) stop holding an action
     * b) do nothing (null)
     * c) do a non-holding action
     * d) start holding an action
     *
     * Note that there is always some action in this list, as it also contains null.
     */
    private fun orderedPerformableActions(gameState: GameState): ArrayList<UndoableAction?> {
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
    private fun shouldCache(gameState: GameState): Boolean {
//        return gameState.grid[
//            gameState.gridLocation(
//                gameState.player.x - gameState.gridX * BlockWidth,
//                gameState.player.y - 0.1
//            )
//        ]?.isSolid == true
        return true
    }
    private fun cache(gameState: GameState) {
        cachedStates.add(CachedState(gameState))
    }
    private fun isInCache(gameState: GameState): Boolean {
        return cachedStates.contains(CachedState(gameState))
    }
    private fun pruneUnusableCache(gameState: GameState) {
        for (cachedState in cachedStates.toList()) {
            if (cachedState.playerX < gameState.player.x)
                cachedStates.remove(cachedState)
        }
    }

    private fun advanceState(gameState: GameState, gameAction: UndoableAction?, updateTime: Double): IUndo {
        ++lastStats.searchedStates
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else {
            val firstUndo = gameAction.applyUndoablyOn(gameState)
            if (gameState.player.nextX(updateTime) + gameState.player.widthPx >= maxX)
                return firstUndo
            val secondUndo = gameState.advanceUndoable(updateTime)
            return UndoFactory.doubleUndo(firstUndo, secondUndo)
        }
    }
}