package cz.woitee.game.algorithms

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.NoActionUndo
import cz.woitee.game.undoing.UndoFactory
import java.security.InvalidParameterException
import java.util.*

/**
 * A special DFS that creates a "delayed twin" state - a gameState that is several frames delayed. This delayed gameState then
 * must perform the same actions as the first one. This should in effect better emulate actions by a player that doesn't have
 * exact accuracy.
 */
class DelayedTwinDFS(val delayTime: Double, maxDepth: Int = 1000, debug: Boolean = true): DFSBase(true, maxDepth, debug) {
    data class TwoStatesUndo(val currentUndo: IUndo, val delayedUndo: IUndo, val actionInDelayed: UndoableAction?)
    //TODO: Change var to val
    data class StackData(var statesUndo: TwoStatesUndo, var actionIx: Int, val possibleActions:List<UndoableAction?>,
                         var playerX: Double, var playerY: Double, var playerYSpeed: Double) {
        val action: UndoableAction?
            get() = possibleActions[actionIx]
        val delayedAction: UndoableAction?
            get() = statesUndo.actionInDelayed
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

    // We need two caches for this
    val statesCache = DelayedTwinDFSCache()

    override fun reset() {
        super.reset()
        delayedState = null
        currentState = null
        dfsStack.clear()
        actionsForDelayedState.clear()
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
            if (dfsStack.count() == 0)
                throw Exception("Empty stack in DelayedTwinDFS. Did you call it on a GameState after a dead end has been found?")
            while (gameState.player.x > dfsStack.peekLast().playerX) {
                dfsStack.pollLast()
            }
            if (gameState.player.x != dfsStack.peekLast().playerX || gameState.player.y != dfsStack.peekLast().playerY || gameState.player.yspeed != dfsStack.peekLast().playerYSpeed) {
                throw InvalidParameterException("Fast forwarding of previously saved state to the one passed as argument failed! " +
                        "(ExpectedX: ${dfsStack.peekLast().playerX} ActualX: ${gameState.player.x}) " +
                        "(ExpectedY: ${dfsStack.peekLast().playerY} ActualY: ${gameState.player.y}) " +
                        "(ExpectedYSpeed: ${dfsStack.peekLast().playerYSpeed} ActualYSpeed: ${gameState.player.yspeed}) "
                )
            }

            // Synchronize end - new additions to grid
            val currentState = this.currentState!!
            val delayedState = this.delayedState!!
            while (gameState.gridX > currentState.gridX) {
                columnCopier.savedColumn = gameState.grid.getColumn(gameState.grid.width - 1)
                synchronized(currentState.gameObjects) {
                    currentState.addColumn(columnCopier.generateNextColumn(currentState))
                }
                synchronized(delayedState.gameObjects) {
                    delayedState.addColumn(columnCopier.generateNextColumn(delayedState))
                }
            }
        }
        synchronized(currentState!!.gameObjects) {
            synchronized(delayedState!!.gameObjects) {
                return searchFromTwoStates()
            }
        }
    }

    fun advanceBothStates(action: UndoableAction?): TwoStatesUndo {
        val stagedAction = actionsForDelayedState.peekFirst()
        val currentUndo = advanceState(currentState!!, action)
        val delayedUndo = advanceDelayedState(action)
        // If the first queued action has changed, then it was used
        val usedAction = if (stagedAction != actionsForDelayedState.peekFirst()) {
            stagedAction
        // If the action was placed on the end, then it wasn't used
        } else if (action == actionsForDelayedState.peekLast()) {
            null
        // Else the action given was used
        } else {
            action
        }
        return TwoStatesUndo(currentUndo, delayedUndo, usedAction)
    }

    protected fun isGameOverInEitherState(): Boolean {
        return currentState!!.isGameOver || delayedState!!.isGameOver
    }

    protected fun applyBothUndo(bothUndo: TwoStatesUndo) {
        bothUndo.currentUndo.undo(currentState!!)
        bothUndo.delayedUndo.undo(delayedState!!)
    }

//     to make it a little simpler, but limited
//        return super.orderedPerformableActions(gameState).filter{ it?.isApplicableOn(delayedState!!) ?: true }
//    }

    /**
     * Advancing delayed state is difficult, because we are sent action applied on current state,
     * and we do not know, whether it can be applied on the delayed already (e.g. can not jump yet because it hasn't landed).
     * If yes, we just push forward normally, otherwise we just add it to queue, and execute it when possible - in next callings
     * of this method.
     */
    protected fun advanceDelayedState(action: UndoableAction?): IUndo {
        val delayedState = delayedState!!

        if (actionsForDelayedState.isNotEmpty()) {
            if (action != null)
                actionsForDelayedState.addLast(action)
            val nextAction = actionsForDelayedState.peekFirst()
            val nextActionApplicable = nextAction.isApplicableOn(delayedState)
            val advancementUndo = if (nextActionApplicable) {
                actionsForDelayedState.pollFirst()
                advanceState(delayedState, nextAction)
            } else {
                advanceState(delayedState, null)
            }
            return object : IUndo {
                override fun undo(gameState: GameState) {
                    advancementUndo.undo(gameState)
                    if (nextActionApplicable)
                        actionsForDelayedState.addFirst(nextAction)
                    if (action != null)
                        actionsForDelayedState.pollLast()
                }
            }
        }

        return if (action == null || action.isApplicableOn(delayedState)) {
            advanceState(delayedState, action)
        } else {
            actionsForDelayedState.addLast(action)
            UndoFactory.doubleUndo(
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

        val placeholderUndo = TwoStatesUndo(NoActionUndo, NoActionUndo, null)
        while (dfsStack.count() < maxDepth && currentState.player.nextX(this.updateTime) + currentState.player.widthPx < maxX) {
            val currentActions: List<UndoableAction?> = orderedPerformableActions(currentState)
            var stackData = StackData(
                    placeholderUndo,
                    0,
                    currentActions,
                    delayedState.player.x,
                    delayedState.player.y,
                    delayedState.player.yspeed
            )
            stackData.statesUndo = advanceBothStates(currentActions[0])
            dfsStack.push(stackData)
            if (dfsStack.count() > lastStats.reachedDepth)
                lastStats.reachedDepth = dfsStack.count()
            if (isGameOverInEitherState() || statesCache.contains(currentState, delayedState)) {
                //backtrack
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (dfsStack.isEmpty()) {
                        // No option but to lose the game
                        return SearchResult(false)
                    }
                    stackData = dfsStack.pop()
                    applyBothUndo(stackData.statesUndo)
                    ++lastStats.backtrackedStates
                    ++stackData.actionIx
                    while (stackData.actionIx < stackData.possibleActions.count()) {
                        stackData.playerYSpeed = delayedState.player.yspeed
                        val undo = advanceBothStates(stackData.action)
                        if (isGameOverInEitherState() || statesCache.contains(currentState, delayedState)) {
                            applyBothUndo(undo)
                            ++lastStats.backtrackedStates
                            ++stackData.actionIx
                        } else {
                            stackData.statesUndo = undo
                            dfsStack.push(stackData)
                            finishedBacktrack = true
                            break
                        }
                    }
                    if (stackData.actionIx >= stackData.possibleActions.count())
                        statesCache.store(currentState, delayedState)
                }
            }
        }
        // rollback last action if a state advance didn't happen (the action performed might not be performable next turn)
        // we'll do some action next search
        if (dfsStack.peekLast().statesUndo.currentUndo is DFSBase.NoStateAdvanceUndo) {
            applyBothUndo(dfsStack.pop().statesUndo)
        }

        return SearchResult(true, dfsStack.peekLast().delayedAction)
    }
}