package Game.Algorithms

import GUI.GamePanelVisualizer
import Game.GameObjects.Player
import Game.GameState
import Game.Undoing.IUndo
import Game.GameActions.IGameAction
import Game.GameActions.IUndoableAction
import Game.Undoing.UndoFactory
import Utils.StopWatch
import Utils.pop
import java.util.*

/**
 * Created by woitee on 30/04/2017.
 */

object DFS {
    var lastStats = SearchStats()
    /**
     * Searches for an action that doesn't lead to death and returns it, or null if it doesn't exist.
     */
    fun searchForAction (gameState: GameState, maxDepth: Int = 1000, updateTime: Double = -1.0, debug:Boolean = false): IGameAction? {
        val _updateTime = if (updateTime < 0) gameState.game.updateTime else updateTime

        lastStats = SearchStats()
        val startTime = System.nanoTime()
        val visualizer = gameState.game.visualizer as GamePanelVisualizer?
        if (debug)
            visualizer?.debugObjects?.clear()

        synchronized(gameState.gameObjects) {
            val undoList = ArrayList<IUndo>()
            val actionList = ArrayList<Int>()

            while (undoList.count() < maxDepth && gameState.player.positionOnScreen() < Game.GameWidth) {
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

    private fun advanceState(gameState: GameState, gameAction: IGameAction?, updateTime: Double): IUndo {
        ++lastStats.searchedStates
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else
            return UndoFactory.doubleUndo(
                (gameAction as IUndoableAction).applyUndoableOn(gameState),
                gameState.advanceUndoable(updateTime)
            )
    }
}