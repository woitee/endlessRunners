package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.DummyObjects
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class ButtonModelTest {
    var game = DummyObjects.createDummyGame()
    var currentState = game.gameState
    var delayedState = game.gameState.makeCopy()
    var buttonModel = ButtonModel(currentState, delayedState, 0.1)

    @BeforeEach
    fun setUp() {
        currentState = game.gameState
        delayedState = game.gameState.makeCopy()
        buttonModel = ButtonModel(currentState, delayedState, 0.1)
    }

    @Test
    fun isReleasable() {
        assertFalse(buttonModel.isReleasable(0))
    }

    @Test
    fun isPressable() {
        assertTrue(buttonModel.isPressable(0))
    }

    @Test
    fun orderedApplicableButtonActions() {
        val list = buttonModel.orderedApplicableButtonActions()
        assertEquals(2, list.count())
        assertNull(list[0])
        assertNotNull(list[1])

        val btnAction = list[1]
        assertEquals(0, btnAction?.button)
        assertEquals(true, btnAction?.isPress)
    }

    @Test
    fun pressableWhenPressingAndReleasing() {
        buttonModel.press(0)
        assertFalse(buttonModel.isPressable(0))
        assertTrue (buttonModel.isReleasable(0))
        buttonModel.release(0)
        assertTrue (buttonModel.isPressable(0))
        assertFalse(buttonModel.isReleasable(0))

        val mockAction = currentState.game.gameDescription.allActions[0] as DummyObjects.MockAction
        assertEquals(2, mockAction.timesApplied)
    }

    @Test
    fun pressableAndReleasableAgainAfterUndoing() {
        val undo = buttonModel.press(0)
        assertFalse(buttonModel.isPressable(0))
        assertTrue (buttonModel.isReleasable(0))
        undo.undo(buttonModel)
        assertTrue (buttonModel.isPressable(0))
        assertFalse(buttonModel.isReleasable(0))

        val mockAction = currentState.game.gameDescription.allActions[0] as DummyObjects.MockAction
        assertEquals(2, mockAction.timesUndone)
    }

    @Test
    fun notAdvancingDisabledDelayedModule() {
        val mockAction = currentState.game.gameDescription.allActions[0] as DummyObjects.MockAction

        buttonModel.currentStateDisabled = true
        val undo = buttonModel.press(0)
        assertEquals(1, mockAction.timesApplied)
        undo.undo(buttonModel)
        assertEquals(1, mockAction.timesUndone)
    }
}