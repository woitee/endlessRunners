package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.algorithms.dfs.CachedState
import cz.woitee.game.algorithms.dfs.DFSBase
import cz.woitee.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.NoActionUndo
import cz.woitee.game.undoing.UndoFactory
import java.lang.Thread.sleep
import java.security.InvalidParameterException
import java.util.*

/**
 * A special DFS that creates a "delayed twin" state - a currentState that is several frames delayed. This delayed currentState then
 * must perform the same actions as the first one. This should in effect better emulate actions by a player that doesn't have
 * exact accuracy.
 */
class DelayedTwinDFS(val delayTime: Double, maxDepth: Int = 1000, debug: Boolean = true,
                     val allowSearchInBeginning: Boolean = false): DFSBase(true, maxDepth, debug) {
    // Type BEGINNING has only currentState undos and Type END has only delayed undos (beginning of algorithm / end of algorithm)
    enum class TwoStatesUndoType { BEGINNING, MIDDLE, END }
    data class TwoStatesUndo(val currentUndo: IUndo, val delayedUndo: IUndo, val actionInDelayed: GameAction?,
                             val type: TwoStatesUndoType = TwoStatesUndoType.MIDDLE)
    //TODO: Change var to val
    data class StackData(var statesUndo: TwoStatesUndo, var actionIx: Int, val possibleActions:List<GameAction?>,
                         var cachedState: CachedState) {
        val action: GameAction?
            get() = possibleActions[actionIx]
        val delayedAction: GameAction?
            get() = statesUndo.actionInDelayed
    }
    // The two states used as variables, inited late by currentState copies
    var delayedState: GameState? = null
    var currentState: GameState? = null
    var framesDelayed: Int = (delayTime / updateTime).toInt()
    var currentlyFramesDelayed: Int = 0

    // DFS stacks to explore - persistent
    val dfsStack = ArrayDeque<StackData>()

    // A list of actions to perform in the delayedState ASAP
    val actionsForDelayedState = ArrayDeque<GameAction>()
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

            currentlyFramesDelayed = 0

            if (!allowSearchInBeginning) {
                for (i in 1 ..framesDelayed) {
                    advanceState(currentState!!, null)
                }
                if (currentState!!.isGameOver) {
                    return SearchResult(false)
                }
                currentlyFramesDelayed = framesDelayed
            }
        } else {
            // Synchronize beginning - stack beginnings
            // Try catch-up, otherwise throw exception
            if (dfsStack.count() == 0)
                throw Exception("Empty stack in DelayedTwinDFS. Did you call it on a GameState after a dead end has been found?")
            while (gameState.player.x > dfsStack.peekLast().cachedState.playerX) {
                dfsStack.pollLast()
            }
            if (gameState.player.x != dfsStack.peekLast().cachedState.playerX ||
                gameState.player.y != dfsStack.peekLast().cachedState.playerY ||
                gameState.player.yspeed != dfsStack.peekLast().cachedState.playerYSpeed) {
                throw InvalidParameterException("Fast forwarding of previously saved state to the one passed as argument failed! " +
                        "(ExpectedX: ${dfsStack.peekLast().cachedState.playerX} ActualX: ${gameState.player.x}) " +
                        "(ExpectedY: ${dfsStack.peekLast().cachedState.playerY} ActualY: ${gameState.player.y}) " +
                        "(ExpectedYSpeed: ${dfsStack.peekLast().cachedState.playerYSpeed} ActualYSpeed: ${gameState.player.yspeed}) "
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
        if (debug)
            return searchFromTwoStates()

        synchronized(currentState!!.gameObjects) {
            synchronized(delayedState!!.gameObjects) {
                return searchFromTwoStates()
            }
        }
    }

    /**
     * Advancing both states and returning appropriate undos. It actually is a little bit more difficult than it sounds.
     * at the beginning we have to advance only the first state, to get to the correct Delay between states. And at the
     * end, we have to only advance the delayed state, to see, if the delayed actions lead to a possible end.
     */
    fun advanceBothStates(action: GameAction?): TwoStatesUndo {
        // If at beginning, search only with currentState
        if (currentlyFramesDelayed < framesDelayed) {
            ++currentlyFramesDelayed
            val currentUndo = advanceState(currentState!!, action)
            if (action != null) actionsForDelayedState.addLast(action)
            val delayedUndo = object : IUndo {
                override fun undo(gameState: GameState) {
                    --currentlyFramesDelayed
                    if (action != null)
                        actionsForDelayedState.pollLast()
                }
            }

            return TwoStatesUndo(currentUndo, delayedUndo, null, type = TwoStatesUndoType.MIDDLE)
        // If at end, search only with delayedState
        } else if (isPlayerAtEnd(currentState!!)) {
            return TwoStatesUndo(NoActionUndo, advanceDelayedState(action), action, type = TwoStatesUndoType.END)
        }
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
//        return super.orderedPerformableActions(currentState).filter{ it?.isApplicableOn(delayedState!!) ?: true }
//    }

    /**
     * Advancing delayed state is difficult, because we are sent action applied on current state,
     * and we do not know, whether it can be applied on the delayed already (e.g. can not jump yet because it hasn't landed).
     * If yes, we just push forward normally, otherwise we just add it to queue, and execute it when possible - in next callings
     * of this method.
     */
    protected fun advanceDelayedState(action: GameAction?): IUndo {
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
        val sleepTime: Long = 0

        val placeholderUndo = TwoStatesUndo(NoActionUndo, NoActionUndo, null)
        while (dfsStack.count() < maxDepth && !isPlayerAtEnd(delayedState)) {
            val currentActions: List<GameAction?> = if (isPlayerAtEnd(currentState))
                orderedPerformableActions(delayedState)
            else
                orderedPerformableActions(currentState)
            var stackData = StackData(
                    placeholderUndo,
                    0,
                    currentActions,
                    CachedState(delayedState)
            )
            if (sleepTime > 0) sleep(sleepTime)
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
                        if (sleepTime > 0) sleep(sleepTime)
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
        // Delayed-Only Actions are not cached
        while (dfsStack.peekFirst().statesUndo.type == TwoStatesUndoType.END) {
            applyBothUndo(dfsStack.pop().statesUndo)
        }
        // rollback last action if a state advance didn't happen (the action performed might not be performable next turn)
        // we'll do some action next search
        if (dfsStack.peekLast().statesUndo.currentUndo is NoStateAdvanceUndo) {
            applyBothUndo(dfsStack.pop().statesUndo)
        }

        while (dfsStack.peekLast().statesUndo.type == TwoStatesUndoType.BEGINNING)
            dfsStack.pollLast()

        return SearchResult(true, dfsStack.peekLast().delayedAction)
    }
}