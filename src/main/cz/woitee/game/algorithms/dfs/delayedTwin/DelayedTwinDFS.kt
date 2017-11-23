package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.algorithms.dfs.CachedState
import cz.woitee.game.algorithms.dfs.DFSBase
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
    data class StackData(var statesUndo: ButtonModel.ButtonUndo,
                         var actionIx: Int, val possibleActions:List<ButtonModel.ButtonAction?>,
                         var cachedState: CachedState) {
        val action: ButtonModel.ButtonAction?
            get() = possibleActions[actionIx]
        val delayedAction: GameAction?
            get() = statesUndo.actionInDelayed
    }
    // ButtonModel holding the two states and realizing what to do with them
    var buttonModel: ButtonModel? = null
    var framesDelayed: Int = (delayTime / updateTime).toInt()
    var currentlyFramesDelayed: Int = 0

    // DFS stacks to explore - persistent
    val dfsStack = ArrayDeque<StackData>()

    var timesCalled: Long = 0

    // We need two caches for this
    val statesCache = DelayedTwinDFSCache()

    override fun reset() {
        super.reset()
        buttonModel = null
        dfsStack.clear()
        timesCalled = 0
    }

    /**
     * Synchronize the two states to the current status and the run the main method.
     */
    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        ++timesCalled
        framesDelayed = (delayTime / updateTime).toInt()
        if (buttonModel == null) {
            // this should be only the first call and first time after reset
            assert(timesCalled == 1L)

            buttonModel = ButtonModel(gameState.makeCopy(), gameState.makeCopy(), updateTime)
            buttonModel!!.delayedStateDisabled = true

            currentlyFramesDelayed = 0

            if (!allowSearchInBeginning) {
                for (i in 1 ..framesDelayed) {
                    buttonModel!!.updateUndoable(null)
                }
                if (buttonModel!!.currentState.isGameOver) {
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
            val buttonModel = buttonModel!!
            for (gridX in buttonModel.currentState.gridX until gameState.gridX) {
                buttonModel.addColumn(gameState.grid.getColumn(gameState.gridX - gridX))
            }
        }
        if (debug)
            return searchFromTwoStates()

        synchronized(buttonModel!!.currentState.gameObjects) {
            synchronized(buttonModel!!.delayedState.gameObjects) {
                return searchFromTwoStates()
            }
        }
    }

    /**
     * Advancing both states and returning appropriate undos. It actually is a little bit more difficult than it sounds.
     * at the beginning we have to advance only the first state, to get to the correct Delay between states. And at the
     * end, we have to only advance the delayed state, to see, if the delayed actions lead to a possible end.
     */
    fun advanceCorrectStates(btnAction: ButtonModel.ButtonAction?): ButtonModel.ButtonUndo {
        val buttonModel = buttonModel!!
        // If at beginning, search only with currentState
        if (currentlyFramesDelayed < framesDelayed) {
            buttonModel.currentStateDisabled = false
            buttonModel.delayedStateDisabled = true
            ++currentlyFramesDelayed
        } else if (isPlayerAtEnd(buttonModel.currentState)) {
            buttonModel.currentStateDisabled = true
            buttonModel.delayedStateDisabled = false
        } else {
            buttonModel.currentStateDisabled = false
            buttonModel.delayedStateDisabled = false
        }

        return buttonModel.updateUndoable(btnAction)
    }

//     to make it a little simpler, but limited
//        return super.orderedPerformableActions(currentState).filter{ it?.isApplicableOn(delayedState!!) ?: true }
//    }

    /**
     * Both states are prepared (the real and the delayed one), just do the search.
     */
    protected fun searchFromTwoStates(): SearchResult {
        val buttonModel = this.buttonModel!!
        val sleepTime: Long = 0

        while (dfsStack.count() < maxDepth && !isPlayerAtEnd(buttonModel.delayedState)) {
            val currentActions: List<ButtonModel.ButtonAction?> = buttonModel.orderedApplicableButtonActions()
//            val currentActions: List<GameAction?> = if (isPlayerAtEnd(currentState))
//                orderedPerformableActions(delayedState)
//            else
//                orderedPerformableActions(currentState)
            var stackData = StackData(
                    buttonModel.noUndo(),
                    0,
                    currentActions,
                    CachedState(buttonModel.delayedState)
            )
            if (sleepTime > 0) sleep(sleepTime)
            stackData.statesUndo = advanceCorrectStates(currentActions[0])
            dfsStack.push(stackData)
            if (dfsStack.count() > lastStats.reachedDepth)
                lastStats.reachedDepth = dfsStack.count()
            if (buttonModel.isGameOver() || statesCache.contains(buttonModel)) {
                //backtrack
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (dfsStack.isEmpty()) {
                        // No option but to lose the game
                        return SearchResult(false)
                    }
                    stackData = dfsStack.pop()
                    stackData.statesUndo.undo(buttonModel)
                    ++lastStats.backtrackedStates
                    ++stackData.actionIx
                    while (stackData.actionIx < stackData.possibleActions.count()) {
                        if (sleepTime > 0) sleep(sleepTime)
                        val undo = advanceCorrectStates(stackData.action)
                        if (buttonModel.isGameOver() || statesCache.contains(buttonModel)) {
                            undo.undo(buttonModel)
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
                        statesCache.store(buttonModel)
                }
            }
        }
        // Delayed-Only Actions are not cached (at End)
        while (dfsStack.peekFirst().statesUndo.disabledStates == ButtonModel.DisabledStates.CURRENT) {
            dfsStack.pop().statesUndo.undo(buttonModel)
        }
        // rollback last action if a state advance didn't happen (the action performed might not be performable next turn)
        // we'll do some action next search
        if (dfsStack.peekLast().statesUndo.currentStateUndo is NoStateAdvanceUndo) {
            dfsStack.pop().statesUndo.undo(buttonModel)
        }

        // Beginning should be polled
        while (dfsStack.peekLast().statesUndo.disabledStates == ButtonModel.DisabledStates.DELAYED)
            dfsStack.pollLast()

        return SearchResult(true, dfsStack.peekLast().delayedAction)
    }
}