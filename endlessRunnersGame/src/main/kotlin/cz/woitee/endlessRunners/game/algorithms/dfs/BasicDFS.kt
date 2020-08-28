package cz.woitee.endlessRunners.game.algorithms.dfs

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.utils.ComputationStopper
import cz.woitee.endlessRunners.utils.pop
import java.util.*

/**
 * A basic BasicDFS implementation for the game. It caches the states to prevent exploring from the same state multiple times.
 * It should be instantiated for each place running it, as the cache (of dead states) is persistent between different searches -
 * this speeds up subsequent lookups.
 *
 * We also cache states that we found dead to not re-explore already explored states.
 */

open class BasicDFS(
    persistentCache: Boolean = true,
    maxDepth: Int = 1000,
    actionEvery: Int = 1,
    debug: Boolean = false,
    computationStopper: ComputationStopper = ComputationStopper()
) : AbstractDFS(persistentCache, maxDepth, actionEvery, debug, computationStopper = computationStopper) {

    constructor(dfs: BasicDFS) : this(dfs.persistentCache, dfs.maxDepth, dfs.actionEvery, dfs.debug, dfs.computationStopper) {
        cachedStates.addAll(dfs.cachedStates)
    }

    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        val undoList = ArrayList<IUndo>()
        val actionList = ArrayList<Int>()
        val possibleActionsList = ArrayList<List<GameButton.StateChange?>>()

        while (undoList.count() < maxDepth && !gameState.isPlayerAtEnd(updateTime)) {
            if (computationStopper.shouldStop) { return SearchResult(false) }
            val currentActions: List<GameButton.StateChange?> =
                if (undoList.count() % actionEvery == 0) orderedPerformableButtonActions(gameState)
                else arrayListOf(null)

            undoList.add(advanceState(gameState, currentActions[0]))

            if (undoList.count() > lastStats.reachedDepth) lastStats.reachedDepth = undoList.count()
            if (gameState.player.x > lastStats.maxPlayerX) lastStats.maxPlayerX = gameState.player.x

            actionList.add(0)
            possibleActionsList.add(currentActions)
            if (gameState.isGameOver || isInCache(gameState)) {
                if (computationStopper.shouldStop) { return SearchResult(false) }
                // backtrack
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
//                println("Plan ${game State.player.x} ${currentState.player.y} ${currentState.player.yspeed}")
            undo.undo(gameState)
        }

        lastStats.cachedStates = cachedStates.count()

        val actions = ArrayList<GameButton.StateChange?>()
        for (i in actionList.indices) {
            val actionIx = actionList[i]
            actions.add(possibleActionsList[i][actionIx])
        }

        return SearchResult(true, actions)
    }
}
