package cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin

import cz.woitee.endlessRunners.game.DummyObjects
import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.WidthBlocks
import cz.woitee.endlessRunners.game.algorithms.dfs.AbstractDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.CachedState
import cz.woitee.endlessRunners.geom.Vector2Double
import cz.woitee.endlessRunners.utils.ComputationStopper
import cz.woitee.endlessRunners.utils.MySerializable
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.Thread.sleep
import java.security.InvalidParameterException
import java.util.*

/**
 * A special BasicDFS that creates a "delayed twin" state - a currentState that is several frames delayed. This delayed currentState then
 * must perform the same actions as the first one. This should in effect better emulate actions by a player that doesn't have
 * exact accuracy.
 */
class DelayedTwinDFS(
    val delayTime: Double,
    maxDepth: Int = 1000,
    actionEvery: Int = 1,
    debug: Boolean = false,
    val allowSearchInBeginning: Boolean = false,
    computationStopper: ComputationStopper = ComputationStopper()
) : AbstractDFS(true, maxDepth, actionEvery, debug, computationStopper = computationStopper), MySerializable {

    data class StackData(
        var statesUndo: ButtonModel.ButtonUndo,
        var actionIx: Int,
        val possibleActions: List<ButtonModel.ButtonAction?>,
        var cachedState: CachedState
    ) : Serializable {
        val action: ButtonModel.ButtonAction?
            get() = possibleActions[actionIx]
    }

    // ButtonModel holding the two states and realizing what to do with them

    var buttonModel: ButtonModel = DummyObjects.createDummyButtonModel()
    var delayFrames: Int = Math.ceil(delayTime / updateTime).toInt()
    var currentlyFramesDelayed: Int = 0
        protected set

    // BasicDFS stacks to explore - persistent
    val dfsStack = ArrayDeque<StackData>()

    var timesCalled: Long = 0
    var sleepTime: Long = 0

    // We need two caches for this
    val statesCache = DelayedTwinDFSCache()

    override val currentlyCachedStates: Int
        get() = statesCache.count()

    override fun init(gameState: GameState) {
        super.init(gameState)

        dfsStack.clear()
        statesCache.clear()
        timesCalled = 0
        currentlyFramesDelayed = 0
        delayFrames = Math.ceil(delayTime / updateTime).toInt()

        buttonModel = ButtonModel(gameState.makeCopy(), gameState.makeCopy(), updateTime)
        buttonModel.delayedState.tag = "delayed"
        buttonModel.currentState.tag = "current"
        buttonModel.delayedStateDisabled = true

        if (!allowSearchInBeginning) {
            for (i in 1 .. delayFrames) {
                buttonModel.updateUndoable(null)
                ++currentlyFramesDelayed
            }
        }
    }

    /**
     * Synchronize the two states to the current status and the run the main method.
     */
    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        ++timesCalled
        delayFrames = Math.ceil(delayTime / updateTime).toInt()

        if (buttonModel.isGameOver())
            return SearchResult(false)

        // Synchronize beginning - stack beginnings
        // Try catch-up, otherwise throw exception
        if (dfsStack.count() > 0) {
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
        }

        synchronizeEndWithGameState(gameState)

        // Do the search
        if (debug)
            return searchFromTwoStates(gameState)

        synchronized(buttonModel.currentState.gameObjects) {
            synchronized(buttonModel.delayedState.gameObjects) {
                return searchFromTwoStates(gameState)
            }
        }
    }

    /**
     * Adds new additions to the grid to the owned model from a GameState.
     */
    fun synchronizeEndWithGameState(gameState: GameState) {
        for (gridX in buttonModel.currentState.gridX until gameState.gridX) {
            buttonModel.addColumn(gameState.grid.getColumn(WidthBlocks - 1))
        }
    }

    /**
     * Advancing both states and returning appropriate undos. It actually is a little bit more difficult than it sounds.
     * at the beginning we have to advance only the first state, to get to the correct Delay between states. And at the
     * end, we have to only advance the delayed state, to see, if the delayed actions lead to a possible end.
     */
    fun advanceCorrectStates(btnAction: ButtonModel.ButtonAction?): ButtonModel.ButtonUndo {
        disableCorrectStates()
        if (buttonModel.disabledStates == ButtonModel.DisabledStates.DELAYED)
            ++currentlyFramesDelayed

        val undo = buttonModel.updateUndoable(btnAction)

        // Recheck once again, to always be actual
        disableCorrectStates()
        return undo
    }

    protected fun disableCorrectStates() {
        if (buttonModel.currentState.isPlayerAtEnd(updateTime)) {
            buttonModel.disabledStates = ButtonModel.DisabledStates.CURRENT
        } else if (currentlyFramesDelayed < delayFrames) {
            buttonModel.disabledStates = ButtonModel.DisabledStates.DELAYED
        } else {
            buttonModel.disabledStates = ButtonModel.DisabledStates.NONE
        }
    }

    /**
     * Override function to force advancing both states - delayed and current.
     * Useful when we want to state that the whole model has updated.
     */
    protected fun advanceBothStates(buttonIx: Int = -1, interaction: GameButton.InteractionType? = null): ButtonModel.ButtonUndo {
        var currentAction: GameButton.StateChange? = null
        var delayedAction: GameButton.StateChange? = null

        if (buttonIx >= 0 && interaction != null) {
            currentAction = buttonModel.currentState.buttons[buttonIx].interact(interaction)
            delayedAction = buttonModel.delayedState.buttons[buttonIx].interact(interaction)
        }

        return ButtonModel.ButtonUndo(
                buttonModel.currentState.advanceUndoableByAction(currentAction, buttonModel.updateTime),
                buttonModel.delayedState.advanceUndoableByAction(delayedAction, buttonModel.updateTime),
                buttonModel.disabledStates
        )
    }

    fun applyUndo(statesUndo: ButtonModel.ButtonUndo) {
        statesUndo.undo(buttonModel)
        if (statesUndo.disabledStates == ButtonModel.DisabledStates.DELAYED)
            --currentlyFramesDelayed
        disableCorrectStates()
    }

    /**
     * Both states are prepared (the real and the delayed one), just do the search.
     */
    protected fun searchFromTwoStates(gameState: GameState): SearchResult {
        val debugPrints = false

        disableCorrectStates()
        while (dfsStack.size < maxDepth && !buttonModel.delayedState.isPlayerAtEnd(updateTime)) {
            if (computationStopper.shouldStop) { return SearchResult(false) }
            val currentActions: List<ButtonModel.ButtonAction?> =
                    if (dfsStack.size % actionEvery == 0) buttonModel.orderedApplicableButtonActions()
                    else arrayListOf(null)
            var stackData = StackData(
                    buttonModel.noUndo(),
                    0,
                    currentActions,
                    CachedState(buttonModel.delayedState)
            )
            if (sleepTime > 0) sleep(sleepTime)
            stackData.statesUndo = advanceCorrectStates(currentActions[0])
            ++lastStats.searchedStates
            dfsStack.push(stackData)

            if (dfsStack.count() > lastStats.reachedDepth) lastStats.reachedDepth = dfsStack.count()
            if (buttonModel.delayedState.player.x > lastStats.maxPlayerX) lastStats.maxPlayerX = buttonModel.delayedState.player.x

            if (buttonModel.isGameOver() || statesCache.contains(buttonModel)) {
                // backtrack
                if (computationStopper.shouldStop) { return SearchResult(false) }
                var finishedBacktrack = false
                while (!finishedBacktrack) {
                    if (dfsStack.isEmpty()) {
                        // No option but to lose the game
                        if (debugPrints) println("From current positions ${buttonModel.delayedState.player.location} ${buttonModel.currentState.player.location} there is nothing to do")
                        return SearchResult(false)
                    }
                    stackData = dfsStack.pop()
                    applyUndo(stackData.statesUndo)
                    ++lastStats.backtrackedStates
                    ++stackData.actionIx
                    while (stackData.actionIx < stackData.possibleActions.count()) {
                        val undo = advanceCorrectStates(stackData.action)
                        ++lastStats.searchedStates
                        if (buttonModel.isGameOver() || statesCache.contains(buttonModel)) {
                            applyUndo(undo)
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
                        statesCache.store(buttonModel, gameState.gameTime)
                }
            }
        }

        if (dfsStack.isEmpty()) {
            return SearchResult(false)
        }
        // Remove states at beginning where we create the delay between the two states
        while (dfsStack.peekLast().statesUndo.disabledStates == ButtonModel.DisabledStates.DELAYED)
            dfsStack.pollLast()

        val actions = ArrayList<GameButton.StateChange?>()
        for (dfsElem in dfsStack.reversed()) {
            val btnAction = dfsElem.action
            val resultAction = when {
                btnAction == null -> null
                btnAction.isPress -> gameState.buttons[btnAction.button].hold
                else -> gameState.buttons[btnAction.button].release
            }
            actions.add(resultAction)
        }

        val listOfPositions = ArrayList<Vector2Double>()
        // Rollback everything that's left
        while (dfsStack.size > 0) {
            if (debugPrints) {
                listOfPositions.add(buttonModel.delayedState.player.location)
                listOfPositions.add(buttonModel.currentState.player.location)
                if (dfsStack.peekFirst().action != null)
                    println("Action ${dfsStack.peekFirst().action} at ${buttonModel.delayedState.player.location}")
            }
            applyUndo(dfsStack.pop().statesUndo)
        }

        if (debugPrints) {
            println("From current positions ${buttonModel.delayedState.player.location} ${buttonModel.currentState.player.location}\n" +
                    "Next should be ${listOfPositions[listOfPositions.size - 2]} ${listOfPositions[listOfPositions.size - 1]} achieved by ${actions[0]}\n")
        }

        assert(dfsStack.size == 0)

        return SearchResult(true, actions)
    }

    override fun onUpdate(updateTime: Double, buttonStateChange: GameButton.StateChange?, gameState: GameState) {
        updateByAction(buttonStateChange, false, gameState)
        synchronizeEndWithGameState(gameState)
    }

    protected fun updateByAction(buttonStateChange: GameButton.StateChange?, releasesOfNonHoldAction: Boolean, gameState: GameState) {
//        println("Updating by action $buttonStateChange")
        if (buttonStateChange == null) {
            advanceBothStates()
        } else {
            val buttonIx = buttonStateChange.gameButton.index

            advanceBothStates(buttonIx, buttonStateChange.interactionType)
        }

        if (buttonModel.currentState.isGameOver) {
//            println("GameOver ${gameState.gridX} $currentlyFramesDelayed")
            val delayedState = buttonModel.delayedState
            init(delayedState)
        }
    }

    override fun pruneUnusableCache(gameState: GameState) {
        statesCache.pruneUnusable(gameState)
    }

    override fun writeObject(oos: ObjectOutputStream): DelayedTwinDFS {
        oos.writeDouble(delayTime)
        oos.writeInt(maxDepth)
        oos.writeBoolean(debug)
        oos.writeBoolean(allowSearchInBeginning)
        buttonModel.writeObject(oos)

        // dfsstack and dfscache is considered internal state and is not serialized

        oos.writeLong(timesCalled)
        oos.writeLong(sleepTime)

        return this
    }

    override fun readObject(ois: ObjectInputStream): DelayedTwinDFS {
        ois.readDouble()
        maxDepth = ois.readInt()
        debug = ois.readBoolean()
        ois.readBoolean()

        buttonModel.readObject(ois)

        // dfsstack and dfscache is considered internal state and is not serialized

        timesCalled = ois.readLong()
        sleepTime = ois.readLong()

        return this
    }
}
