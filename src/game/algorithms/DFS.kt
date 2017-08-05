package game.algorithms

import gui.GamePanelVisualizer
import game.BlockWidth
import game.gameObjects.Player
import game.GameState
import game.undoing.IUndo
import game.gameActions.GameAction
import game.gameActions.UndoableAction
import game.undoing.UndoFactory
import utils.pop
import java.util.*

/**
 * Created by woitee on 30/04/2017.
 */

object DFS {
    var lastStats = SearchStats()
    /**
     * Searches for an action that doesn't lead to death and returns it, or null if it doesn't exist.
     */
    fun searchForAction (gameState: GameState, maxDepth: Int = 1000, updateTime: Double = -1.0, debug:Boolean = false): GameAction? {
        val _updateTime = if (updateTime < 0) gameState.game.updateTime else updateTime

        lastStats = SearchStats()
        val startTime = System.nanoTime()
        val visualizer = gameState.game.visualizer as GamePanelVisualizer?
        if (debug)
            visualizer?.debugObjects?.clear()

        synchronized(gameState.gameObjects) {
            val undoList = ArrayList<IUndo>()
            val actionList = ArrayList<Int>()

            val maxX = (gameState.gridX + gameState.grid.width - 1) * BlockWidth
            while (undoList.count() < maxDepth && gameState.player.nextX(_updateTime) < maxX) {
                undoList.add(advanceState(gameState, null, _updateTime))
                if (undoList.count() > lastStats.reachedDepth)
                    lastStats.reachedDepth = undoList.count()
                actionList.add(-1)
                if (gameState.isGameOver) {
                    //backtrack
                    var finishedBacktrack = false
                    while (!finishedBacktrack) {
                        if (undoList.isEmpty()) {
                            lastStats.timeTaken = ((System.nanoTime() - startTime)/1000).toDouble() / 1000
                            // No option but to lose the game
                            return null
                        }
                        undoList.pop().undo(gameState)
                        ++lastStats.backtrackedStates
                        var action = actionList.pop() + 1
                        val actions = gameState.getPerformableActions()
                        while (action < actions.count()) {
                            val undo = advanceState(gameState, actions[action], _updateTime)
                            if (gameState.isGameOver) {
                                undo.undo(gameState)
                                ++lastStats.backtrackedStates
                                ++action
                            } else {
                                undoList.add(undo)
                                actionList.add(action)
                                finishedBacktrack = true
                                break
                            }
                        }
                    }
                }
            }

            var count = 0
            for (undo in undoList.asReversed()) {
                if (debug && count++ % 15 == 0)
                    visualizer?.debugObjects?.add(Player(gameState.player.x, gameState.player.y))
                undo.undo(gameState)
            }

            val actionIx = actionList[0]
            val action = if (actionIx == -1) null else gameState.getPerformableActions()[actionIx]
            lastStats.success = true
            lastStats.timeTaken = (System.nanoTime() - startTime).toDouble() / 1000000
            return action
        }
    }

    private fun advanceState(gameState: GameState, gameAction: GameAction?, updateTime: Double): IUndo {
        ++lastStats.searchedStates
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else
            return UndoFactory.doubleUndo(
                (gameAction as UndoableAction).applyUndoablyOn(gameState),
                gameState.advanceUndoable(updateTime)
            )
    }
}