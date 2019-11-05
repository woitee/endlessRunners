package cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo
import cz.woitee.endlessRunners.utils.MySerializable
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*

/**
 * A model to encompass both states of Delayed Twin DFS, and ensure that they both press the same buttons.
 *
 * Essentially acts as a "larger GameState" - actions can be performed, time can advance and game over can occur.
 *
 * @param currentState The current (ahead of the other) state of the game
 * @param delayedState The delayed (behind the other) state of the game
 * @param updateTime The default update time of each step
 */
class ButtonModel(var currentState: GameState, var delayedState: GameState, val updateTime: Double) : MySerializable {
    enum class ThreeStateInteraction { NONE, PRESS, RELEASE }
    enum class DisabledStates { NONE, CURRENT, DELAYED, BOTH }
    data class ButtonAction(val button: Int, val isPress: Boolean)
    open class ButtonUndo(val currentStateUndo: IUndo, val delayedStateUndo: IUndo, val disabledStates: DisabledStates) : Serializable {
        constructor(buttonUndo: ButtonUndo) : this(buttonUndo.currentStateUndo, buttonUndo.delayedStateUndo, buttonUndo.disabledStates)
        open fun undo(currentState: GameState, delayedState: GameState) {
            currentStateUndo.undo(currentState)
            delayedStateUndo.undo(delayedState)
        }
        fun undo(buttonModel: ButtonModel) {
            undo(buttonModel.currentState, buttonModel.delayedState)
        }
    }
    var disabledStates: DisabledStates
        get() =
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
        set(value) {
            when (value) {
                DisabledStates.NONE -> { currentStateDisabled = false; delayedStateDisabled = false }
                DisabledStates.DELAYED -> { currentStateDisabled = false; delayedStateDisabled = true }
                DisabledStates.CURRENT -> { currentStateDisabled = true; delayedStateDisabled = false }
                DisabledStates.BOTH -> { currentStateDisabled = true; delayedStateDisabled = true }
            }
        }

    fun noUndo() = ButtonUndo(NoUndo, NoUndo, disabledStates)
    fun threeStateInteractionFromBool(isPress: Boolean): ThreeStateInteraction {
        return if (isPress) ThreeStateInteraction.PRESS else ThreeStateInteraction.RELEASE
    }

    val maxButton: Int
        get() = currentState.allActions.lastIndex

    /**
     * We only update buttonStates in non-disabled states, so we check those when in need of info how to .
     */
    fun isPressed(button: Int): Boolean {
        return when (disabledStates) {
            ButtonModel.DisabledStates.NONE -> {
                assert(currentState.buttons[button].isPressed == delayedState.buttons[button].isPressed)
                currentState.buttons[button].isPressed
            }
            ButtonModel.DisabledStates.CURRENT -> delayedState.buttons[button].isPressed
            ButtonModel.DisabledStates.DELAYED -> currentState.buttons[button].isPressed
            ButtonModel.DisabledStates.BOTH -> false
        }
    }

    val allButtonActions = ArrayList<ArrayList<ButtonAction>>()

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

    fun setStates(gameState: GameState, delayedState: GameState) {
        this.currentState = gameState
        this.delayedState = delayedState
    }

    /**
     * The whole button model is game over if either of its states are game over.
     */
    fun isGameOver() = currentState.isGameOver || delayedState.isGameOver

    /**
     * Actual methods used in working with the ButtonModel.
     */

    fun addColumn(column: List<GameObject?>): List<GameObject?> {
        columnCopier.savedColumn = column
        synchronized(currentState.gameObjects) {
            currentState.addColumn(columnCopier.generateNextColumn(currentState))
        }
        synchronized(delayedState.gameObjects) {
            return delayedState.addColumn(columnCopier.generateNextColumn(delayedState))
        }
    }

    fun undoAddColumn(column: List<GameObject?>): List<GameObject?> {
        columnCopier.savedColumn = column
        synchronized(currentState.gameObjects) {
            currentState.undoAddColumn(columnCopier.generateNextColumn(currentState))
        }
        synchronized(delayedState.gameObjects) {
            return delayedState.undoAddColumn(columnCopier.generateNextColumn(delayedState))
        }
    }

    fun isReleasable(button: Int): Boolean {
        return (!currentStateDisabled && currentState.buttons[button].makesSenseToRelease) ||
                (!delayedStateDisabled && delayedState.buttons[button].makesSenseToRelease)
    }

    fun isPressable(button: Int): Boolean {
        return (!currentStateDisabled && currentState.buttons[button].makesSenseToPress) ||
                (!delayedStateDisabled && delayedState.buttons[button].makesSenseToPress)
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
        // Try to isPress some buttons
        for (button in 0 .. maxButton) {
            if (isPressable(button))
                list.add(getButtonAction(button, true))
        }
        return list
    }

    internal fun press(button: Int): ButtonUndo = updateStatesUndoably(button, ThreeStateInteraction.PRESS)
    internal fun release(button: Int): ButtonUndo = updateStatesUndoably(button, ThreeStateInteraction.RELEASE)
    internal fun noAction(): ButtonUndo = updateStatesUndoably(-1, ThreeStateInteraction.NONE)

    protected fun updateStatesUndoably(button: Int, press: ThreeStateInteraction): ButtonUndo {
        val currentStateChange = when (press) {
            ButtonModel.ThreeStateInteraction.NONE -> null
            ButtonModel.ThreeStateInteraction.PRESS -> currentState.buttons[button].hold
            ButtonModel.ThreeStateInteraction.RELEASE -> currentState.buttons[button].release
        }
        val delayedStateChange = when (press) {
            ButtonModel.ThreeStateInteraction.NONE -> null
            ButtonModel.ThreeStateInteraction.PRESS -> delayedState.buttons[button].hold
            ButtonModel.ThreeStateInteraction.RELEASE -> delayedState.buttons[button].release
        }

        return ButtonUndo(
                if (currentStateDisabled) NoUndo else currentState.advanceUndoableByAction(currentStateChange, updateTime),
                if (delayedStateDisabled) NoUndo else delayedState.advanceUndoableByAction(delayedStateChange, updateTime),
                disabledStates
        )
    }

    fun updateUndoable(btnAction: ButtonAction?): ButtonUndo {
        return if (btnAction == null) noAction()
        else updateStatesUndoably(btnAction.button, threeStateInteractionFromBool(btnAction.isPress))
    }

    fun heldButtonsAsFlags(): Int {
        return currentState.heldButtonsAsFlags()
    }

    // ======================
    //      SERIALIZATION
    // ======================

    override fun writeObject(oos: ObjectOutputStream): ButtonModel {
        oos.writeInt(maxButton)
        for (i in 0 .. maxButton) {
            // backwards compatibility
            oos.writeBoolean(currentState.buttons[i].isPressed)
        }
        oos.writeBoolean(currentStateDisabled)
        oos.writeBoolean(delayedStateDisabled)
        currentState.writeObject(oos)
        delayedState.writeObject(oos)

        return this
    }

    override fun readObject(ois: ObjectInputStream): ButtonModel {
        ois.readInt()
        for (i in 0 .. maxButton) {
            // backwards compatibility
            ois.readBoolean()
        }
        currentStateDisabled = ois.readBoolean()
        delayedStateDisabled = ois.readBoolean()
        currentState.readObject(ois)
        delayedState.readObject(ois)

        return this
    }
}
