package cz.woitee.game.algorithms

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.UndoFactory
import java.security.InvalidParameterException
import java.util.*

/**
 * A special DFS that creates a "delayed twin" state - a gameState that is several frames delayed. This delayed gameState then
 * must perform the same actions as the first one. This should in effect better emulate actions by a player that doesn't have
 * exact accuracy.
 */
class DelayedTwinDFS(val delayTime: Double, maxDepth: Int = 1000, debug: Boolean = true): DFSBase(true, maxDepth, debug) {
    data class TwoStatesUndo(val currentUndo: IUndo, val delayedUndo: IUndo)
    data class StackData(val statesUndo: TwoStatesUndo, val actionIx: Int, val possibleActions:List<UndoableAction?>, val playerX: Double) {
        val action: UndoableAction?
            get() = possibleActions[actionIx]
    }
    // The two states used as variables, inited late by gameState copies
    var delayedState: GameState? = null
    var currentState: GameState? = null
    var framesDelayed: Int = (delayTime / updateTime).toInt()

    // DFS stacks to explore - persistent
    val dfsStack = ArrayDeque<StackData>()

    // A list of actions to perform in the delayedState ASAP
    val actionsForDelayedState = ArrayDeque<UndoableAction>()
    var timesCalled: Long = 0

    val columnCopier = ColumnCopyingLevelGenerator()

    override fun reset() {
        super.reset()
        delayedState = null
        currentState = null
        dfsStack.clear()
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
            // Synchronize beginning - stack beginnings
            // Try catch-up, otherwise throw exception
            while (gameState.player.x > dfsStack.peekLast().playerX) {
                dfsStack.pollLast()
            }
            if (gameState.player.x != dfsStack.peekLast().playerX) {
                throw InvalidParameterException("Fast forwarding of previously saved state to the one passed as argument failed! " +
                        "Expected: ${dfsStack.peekLast().playerX} Actual: ${gameState.player.x}")
            }
            // Synchronize end - new additions to grid
            val currentState = this.currentState!!
            val delayedState = this.delayedState!!
            if (gameState.gridX > currentState.gridX) {
                columnCopier.savedColumn = gameState.grid.getColumn(gameState.grid.width - 1)
                synchronized(currentState.gameObjects) {
                    currentState.addColumn(columnCopier)
                }
                synchronized(delayedState.gameObjects) {
                    delayedState.addColumn(columnCopier)
                }
            }
        }
        synchronized(currentState!!.gameObjects) {
            synchronized(delayedState!!.gameObjects) {
                return searchFromTwoStates()
            }
        }
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
    protected fun searchFromTwoStates(): SearchResult {
        val currentState = this.currentState!!
        val delayedState = this.delayedState!!

        while (dfsStack.count() < maxDepth && currentState.player.nextX(this.updateTime) + currentState.player.widthPx < maxX) {
            val currentActions: List<UndoableAction?> = orderedPerformableActions(currentState)
            dfsStack.push(StackData(
                advanceBothStates(currentActions[0]),
                0,
                currentActions,
                delayedState.player.x
            ))
            if (dfsStack.count() > lastStats.reachedDepth)
                lastStats.reachedDepth = dfsStack.count()
            if (isGameOverInEitherState() || isInCache(currentState)) {
                //backtrack
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (dfsStack.isEmpty()) {
                        // No option but to lose the game
                        return SearchResult(false)
                    }
                    val stackData = dfsStack.pop()
                    applyBothUndo(stackData.statesUndo)
                    ++lastStats.backtrackedStates
                    var actionIx = stackData.actionIx + 1
                    val actions = stackData.possibleActions
                    while (actionIx < actions.count()) {
                        val undo = advanceBothStates(actions[actionIx])
                        if (isGameOverInEitherState() || isInCache(currentState)) {
                            applyBothUndo(undo)
                            ++lastStats.backtrackedStates
                            ++actionIx
                        } else {
                            dfsStack.push(StackData(
                                undo,
                                actionIx,
                                actions,
                                delayedState.player.x
                            ))
                            finishedBacktrack = true
                            break
                        }
                    }
                    if (actionIx >= actions.count())
                        if (shouldCache(currentState)) cache(currentState)
                }
            }
        }
        // rollback last action if a state advance didn't happen (the action performed might not be performable next turn)
        // we'll do some action next search
        if (dfsStack.peekLast().statesUndo.currentUndo is DFSBase.NoStateAdvanceUndo) {
            applyBothUndo(dfsStack.pop().statesUndo)
        }

        return SearchResult(true, dfsStack.peekLast().action)
    }
}