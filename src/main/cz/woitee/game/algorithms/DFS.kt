package cz.woitee.game.algorithms

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.utils.pop
import java.util.*

/**
 * A basic DFS implementation for the game. It caches the states to prevent exploring from the same state multiple times.
 * It should be instantiated for each place running it, as the cache (of dead states) is persistent between different searches -
 * this speeds up subsequent lookups.
 *
 * Created by woitee on 30/04/2017.
 */

open class DFS (persistentCache:Boolean = true, maxDepth: Int = 1000, debug: Boolean = false): DFSBase(persistentCache, maxDepth, debug) {
    constructor(dfs: DFS): this(dfs.persistentCache, dfs.maxDepth, dfs.debug) {
        cachedStates.addAll(dfs.cachedStates)
    }

    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        val undoList = ArrayList<IUndo>()
        val actionList = ArrayList<Int>()
        val possibleActionsList = ArrayList<List<UndoableAction?>>()

        while (undoList.count() < maxDepth && gameState.player.nextX(this.updateTime) + gameState.player.widthPx < maxX) {
            val currentActions: List<UndoableAction?> = orderedPerformableActions(gameState)
            undoList.add(advanceState(gameState, currentActions[0]))

            if (undoList.count() > lastStats.reachedDepth)
                lastStats.reachedDepth = undoList.count()
            actionList.add(0)
            possibleActionsList.add(currentActions)
            if (gameState.isGameOver || isInCache(gameState)) {
                //backtrack
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (undoList.isEmpty()) {
                        // No option but to lose the game
                        return SearchResult(false)
                    }
                    undoList.pop().undo(gameState)
                    ++lastStats.backtrackedStates
                    var action = actionList.pop() + 1
                    val actions = possibleActionsList.pop()
                    while (action < actions.count()) {
                        val undo = advanceState(gameState, actions[action])
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
            // uncomment to print plan as a road of player
//                println("Plan ${game State.player.x} ${gameState.player.y} ${gameState.player.yspeed}")
            undo.undo(gameState)
        }

        val actionIx = actionList[0]
        val action = possibleActionsList[0][actionIx]
        return SearchResult(true, action)
    }
}