package cz.woitee.game

import cz.woitee.game.actions.abstract.HoldButtonAction
import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.game.playerControllers.NoActionPlayerController
import org.junit.jupiter.api.Assertions.*

internal class GameStateTest {
    @org.junit.jupiter.api.Test
    fun heldActionRegistrationTest() {
        val game = Game(
                FlatLevelGenerator(),
                NoActionPlayerController(),
                null,
                gameDescription = DummyObjects.MockGameDescription()
        )

        // Preparation
        val mockHoldAction = game.gameDescription.allActions[1]
        assertTrue(mockHoldAction is HoldButtonAction)

        val gameState = game.gameState
        gameState.advanceByAction(null, game.updateTime)
        assertEquals(0, gameState.heldActions.count())

        val mockButton = gameState.buttons[1]
        assertSame(mockButton.action, mockHoldAction)

        // Lets do two steps and see
        gameState.advanceByAction(mockButton.hold, game.updateTime)
        assertTrue(gameState.buttons[1].isPressed)
        assertEquals(1, gameState.heldActions.count())
        assertEquals(mockHoldAction, gameState.heldActions.keys.first())
        assertEquals(game.updateTime, gameState.heldActions[mockHoldAction])

        gameState.advanceByAction(mockButton.release, game.updateTime)
        assertFalse(gameState.buttons[1].isPressed)
        gameState.advance(game.updateTime)
        assertEquals(0, game.gameState.heldActions.count())
    }

    @org.junit.jupiter.api.Test
    fun heldActionRegistrationUndoableTest() {
        val game = Game(
                FlatLevelGenerator(),
                NoActionPlayerController(),
                null,
                gameDescription = DummyObjects.MockGameDescription()
        )

        // Preparation
        val mockHoldAction = game.gameDescription.allActions[1]
        assertTrue(mockHoldAction is HoldButtonAction)

        val gameState = game.gameState
        assertEquals(0, gameState.heldActions.count())

        val mockButton = gameState.buttons[1]
        assertSame(mockButton.action, mockHoldAction)

        // Check state before
        assertFalse(gameState.buttons[1].isPressed)
        assertEquals(0, game.gameState.heldActions.count())

        // Lets do two steps there and see
        val undo1 = gameState.advanceUndoableByAction(null, game.updateTime)
        assertEquals(game.updateTime, gameState.gameTime)
        val undo2 = gameState.advanceUndoableByAction(mockButton.hold, game.updateTime)
        assertTrue(gameState.buttons[1].isPressed)
        assertEquals(1, gameState.heldActions.count())
        assertEquals(mockHoldAction, gameState.heldActions.keys.first())
        assertEquals(game.updateTime, gameState.heldActions[mockHoldAction])

        val undo3 = gameState.advanceUndoableByAction(mockButton.release, game.updateTime)
        assertFalse(gameState.buttons[1].isPressed)
        assertEquals(0, game.gameState.heldActions.count())

        // Now lets undo
        undo3.undo(gameState)
        assertTrue(gameState.buttons[1].isPressed)
        assertEquals(1, gameState.heldActions.count())
        assertEquals(mockHoldAction, gameState.heldActions.keys.first())
        assertEquals(game.updateTime, gameState.heldActions[mockHoldAction])

        undo2.undo(gameState)
        assertFalse(gameState.buttons[1].isPressed)
        assertEquals(0, game.gameState.heldActions.count())

        undo1.undo(gameState)
        assertFalse(gameState.buttons[1].isPressed)
        assertEquals(0, game.gameState.heldActions.count())
    }
}