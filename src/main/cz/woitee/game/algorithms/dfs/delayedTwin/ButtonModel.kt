package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.abstract.HoldAction
import cz.woitee.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.game.objects.GameObject
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.NoUndo
import cz.woitee.utils.arrayList
import java.util.*


class ButtonModel(var currentState: GameState, var delayedState: GameState, val updateTime: Double) {
    enum class DisabledStates { NONE, CURRENT, DELAYED, BOTH }
    data class ButtonAction(val button: Int, val isPress: Boolean = true)
    open class ButtonUndo(val currentStateUndo: IUndo, val delayedStateUndo: IUndo,
                          val actionInDelayed: GameAction?, val disabledStates: DisabledStates) {
        constructor(buttonUndo: ButtonUndo) : this(buttonUndo.currentStateUndo, buttonUndo.delayedStateUndo, buttonUndo.actionInDelayed, buttonUndo.disabledStates)
        open fun undo(currentState: GameState, delayedState: GameState) {
            currentStateUndo.undo(currentState)
            delayedStateUndo.undo(delayedState)
        }
        fun undo(buttonModel: ButtonModel) {
            undo(buttonModel.currentState, buttonModel.delayedState)
        }
    }
    val disabledStates: DisabledStates
        get () =
            if (currentStateDisabled) {
                if (delayedStateDisabled)
                    DisabledStates.BOTH
                else
                    DisabledStates.CURRENT
            } else {
                if (delayedStateDisabled)
                    DisabledStates.DELAYED
                else
                    DisabledStates.NONE
            }

    fun noUndo() = ButtonUndo(NoUndo, NoUndo, null, disabledStates)

    val button2Action = currentState.allActions
    val maxButton: Int
        get() = button2Action.count() - 1

    val allButtonActions = ArrayList<ArrayList<ButtonAction>>()
    val buttonsPressStates = arrayList(button2Action.count(), { false })

    var currentStateDisabled = false
    var delayedStateDisabled = false

    val columnCopier = ColumnCopyingLevelGenerator()

    init {
        for (i in 0 .. maxButton) {
            allButtonActions.add(ArrayList(2))
            for (press in 0 .. 1) {
                allButtonActions[i].add(ButtonAction(i, press == 1))
            }
        }
        reset()
    }

    fun getButtonAction(button: Int, isPress: Boolean): ButtonAction = allButtonActions[button][if (isPress) 1 else 0]

    fun reset() {
    }

    fun actionToButton(gameAction: GameAction): Int = button2Action.indexOf(gameAction)

    fun setStates(gameState: GameState, delayedState: GameState) {
        this.currentState = gameState
        this.delayedState = delayedState
    }

    fun isGameOver() = currentState.isGameOver || delayedState.isGameOver

    /**
     * Actual methods used in working with the ButtonModel.
     */

    fun addColumn(column: List<GameObject?>) {
        columnCopier.savedColumn = column
        synchronized(currentState.gameObjects) {
            currentState.addColumn(columnCopier.generateNextColumn(currentState))
        }
        synchronized(delayedState.gameObjects) {
            delayedState.addColumn(columnCopier.generateNextColumn(delayedState))
        }
    }

    fun isReleasable(button: Int): Boolean {
        if (!buttonsPressStates[button])
            return false

        val action = button2Action[button]
        if (action !is HoldAction)
            return true
        return (!currentStateDisabled && action.canBeStoppedApplyingOn(currentState))
                || (!delayedStateDisabled && action.canBeStoppedApplyingOn(delayedState))
    }

    fun isPressable(button: Int): Boolean {
        if (buttonsPressStates[button])
            return false

        val action = button2Action[button]
        return (!currentStateDisabled && action.isApplicableOn(currentState))
                || (!delayedStateDisabled && action.isApplicableOn(delayedState))
    }

    fun orderedApplicableButtonActions(): ArrayList<ButtonAction?> {
        val list = ArrayList<ButtonAction?>()

        // Try to release any button
        for (button in 0 .. maxButton) {
            if (isReleasable(button))
                list.add(getButtonAction(button, false))
        }
        // Do nothing
        list.add(null)
        // Try to press some buttons
        for (button in 0 .. maxButton) {
            if (isPressable(button))
                list.add(getButtonAction(button, true))
        }
        return list
    }

    internal fun press(button: Int): ButtonUndo = toggleButton(button, true)

    internal fun release(button: Int): ButtonUndo = toggleButton(button, false)

    protected fun toggleButton(button: Int, press: Boolean): ButtonUndo {
        if ((press && !isPressable(button)) || (!press && !isReleasable(button)))
            return updateStatesUndoably()

        buttonsPressStates[button] = press
        val statesUndo = updateStatesUndoably()
        return object : ButtonUndo(statesUndo) {
            override fun undo(currentState: GameState, delayedState: GameState) {
                buttonsPressStates[button] = !press
                super.undo(currentState, delayedState)
            }
        }
    }

    protected fun updateStatesUndoably(): ButtonUndo {
        val currentAction = if (currentStateDisabled) null else getActionFromButtons(currentState)
        val delayedAction = if (delayedStateDisabled) null else getActionFromButtons(delayedState)

        return ButtonUndo(
                if (currentStateDisabled) NoUndo else DFSUtils.advanceGameStateSafely(currentState, currentAction, updateTime),
                if (delayedStateDisabled) NoUndo else DFSUtils.advanceGameStateSafely(delayedState, delayedAction, updateTime),
                delayedAction,
                disabledStates
        )
    }

    protected fun getActionFromButtons(gameState: GameState): GameAction? {
        // try to release an already happening action
        for (button in buttonsPressStates.indices) {
            val action = button2Action[button]
            if (!buttonsPressStates[button] && action is HoldAction && action.canBeStoppedApplyingOn(gameState)) {
                return action.asStopAction
            }
        }
        // try to press an action
        for (button in buttonsPressStates.indices) {
            val action = button2Action[button]
            if (buttonsPressStates[button] && action.isApplicableOn(gameState))
                return action
        }
        // do nothing
        return null
    }

    fun updateUndoable(btnAction: ButtonAction?): ButtonUndo {
        if (btnAction == null)
            return updateStatesUndoably()
        return toggleButton(btnAction.button, btnAction.isPress)
    }
}