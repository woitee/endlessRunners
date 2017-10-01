package cz.woitee.game.algorithms

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.UndoFactory
import cz.woitee.utils.pop
import java.security.InvalidParameterException
import java.util.*

/**
 * A special DFS that creates a "delayed twin" state - a gameState that is several frames delayed. This delayed gameState then
 * must perform the same actions as the first one. This should in effect better emulate actions by a player that doesn't have
 * exact accuracy.
 */
class DelayedTwinDFS(val delayTime: Double, maxDepth: Int = 1000, debug: Boolean = true): DFSBase(true, maxDepth, debug) {
    data class TwoStatesUndo(val currentUndo: IUndo, val delayedUndo: IUndo)
    // The two states used as variables, inited late by gameState copies
    var delayedState: GameState? = null
    var currentState: GameState? = null
    var framesDelayed: Int = (delayTime / updateTime).toInt()

    // DFS stacks to explore - persistent
    val undoStack = ArrayDeque<TwoStatesUndo>()
    val actionStack = ArrayDeque<Int>()
    val possibleActionsStack = ArrayDeque<List<UndoableAction?>>()
    val playerXStack = ArrayDeque<Double>()

    // A list of actions to perform in the delayedState ASAP
    val actionsForDelayedState = ArrayDeque<UndoableAction>()
    var timesCalled: Long = 0

    override fun reset() {
        super.reset()
        delayedState = null
        currentState = null
        undoStack.clear()
        actionStack.clear()
        possibleActionsStack.clear()
        playerXStack.clear()
        timesCalled = 0
    }

    /**
     * Synchronize the two states to the current status and the run the main method.
     */
    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        ++timesCalled
        framesDelayed = (delayTime / updateTime).toInt()
        if (currentState == null) {
            // this should be only the first call and first time after reset
            assert(timesCalled == 1L)

            currentState = gameState.makeCopy()
            delayedState = gameState.makeCopy()

            for (i in 1 ..framesDelayed) {
                advanceState(currentState!!, null)
            }
            if (currentState!!.isGameOver) {
                return SearchResult(false)
            }
        } else {
            // Try catch-up, otherwise throw exception
            // Synchronize beginning - stack beginnings
            while (gameState.player.x > playerXStack.peekLast()) {
                undoStack.pollLast()
                playerXStack.pollLast()
                possibleActionsStack.pollLast()
                actionStack.pollLast()
            }
            if (gameState.player.x != playerXStack.peekLast()) {
                throw InvalidParameterException("Fast forwarding of previously saved state to the one passed as argument failed!")
            }
            // Synchronize end - new additions to grid
            val currentState = this.currentState!!
            val delayedState = this.delayedState!!
            if (gameState.gridX > currentState.gridX) {
                val column = gameState.grid.getColumn(gameState.grid.width - 1)
                for (i in column.indices) {
                    column[i] = column[i]?.makeCopy()
                }
                synchronized(currentState.gameObjects) {
                    currentState.addColumn(column)
                }
                for (i in column.indices) {
                    column[i] = column[i]?.makeCopy()
                }
                synchronized(delayedState.gameObjects) {
                    delayedState.addColumn(column)
                }
            }
        }

        return searchFromTwoStates(updateTime)
    }

    protected fun advanceBothStates(action: UndoableAction?): TwoStatesUndo {
        return TwoStatesUndo(
            advanceState(currentState!!, action),
            advanceDelayedState(action)
        )
    }

    protected fun isGameOverInEitherState(): Boolean {
        return currentState!!.isGameOver || delayedState!!.isGameOver
    }

    protected fun applyBothUndo(bothUndo: TwoStatesUndo) {
        bothUndo.delayedUndo.undo(delayedState!!)
        bothUndo.currentUndo.undo(currentState!!)
    }

    /**
     * Advancing delayed state is difficult, because we are sent action applied on current state,
     * and we do not know, whether it can be applied on the delayed already (e.g. can not jump yet because it hasn't landed).
     * If yes, we just push forward normally, otherwise we just add it to queue, and execute it when possible - in next callings
     * of this method.
     */
    protected fun advanceDelayedState(action: UndoableAction?): IUndo {
        val delayedState = delayedState!!

        if (actionsForDelayedState.isNotEmpty()) {
            val nextAction = if (actionsForDelayedState.peekFirst().isApplicableOn(delayedState)) {
                actionsForDelayedState.pollFirst()
            } else {
                null
            }
            val stateUndo = advanceState(delayedState, nextAction)
            if (nextAction == null)
                return stateUndo
            else {
                return UndoFactory.doubleUndo(
                        object : IUndo {
                            override fun undo(gameState: GameState) { actionsForDelayedState.push(nextAction) }
                        },
                        stateUndo
                )
            }
        }

        if (action == null || action.isApplicableOn(delayedState)) {
            return advanceState(delayedState, action)
        } else {
            actionsForDelayedState.add(action)
            return UndoFactory.doubleUndo(
                    object : IUndo {
                        override fun undo(gameState: GameState) { actionsForDelayedState.pollLast() }
                    },
                    advanceState(delayedState, null)
            )
        }
    }

    /**
     * Both states are prepared (the real and the delayed one), just do the search.
     */
    protected fun searchFromTwoStates(updateTime: Double): SearchResult {
        val currentState = this.currentState!!
        val delayedState = this.delayedState!!

        while (undoStack.count() < maxDepth && currentState.player.nextX(this.updateTime) + currentState.player.widthPx < maxX) {
            val currentActions: List<UndoableAction?> = orderedPerformableActions(currentState)
            playerXStack.push(delayedState.player.x)
            undoStack.push(advanceBothStates(currentActions[0]))

            if (undoStack.count() > lastStats.reachedDepth)
                lastStats.reachedDepth = undoStack.count()
            actionStack.add(0)
            possibleActionsStack.add(currentActions)
            if (isGameOverInEitherState() || isInCache(currentState)) {
                //backtrack
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (undoStack.isEmpty()) {
                        // No option but to lose the game
                        return SearchResult(false)
                    }
                    playerXStack.pop()
                    applyBothUndo(undoStack.pop())
                    ++lastStats.backtrackedStates
                    var action = actionStack.pop() + 1
                    val actions = possibleActionsStack.pop()
                    while (action < actions.count()) {
                        val undo = advanceBothStates(actions[action])
                        if (isGameOverInEitherState() || isInCache(currentState)) {
                            applyBothUndo(undo)
                            ++lastStats.backtrackedStates
                            ++action
                        } else {
                            undoStack.push(undo)
                            actionStack.push(action)
                            possibleActionsStack.push(actions)
                            playerXStack.push(delayedState.player.x)
                            finishedBacktrack = true
                            break
                        }
                    }
                    if (action >= actions.count())
                        if (shouldCache(currentState)) cache(currentState)
                }
            }
        }

        val actionIx = actionStack.peekLast()
        val action = possibleActionsStack.peekLast()[actionIx]
        return SearchResult(true, action)
    }
}