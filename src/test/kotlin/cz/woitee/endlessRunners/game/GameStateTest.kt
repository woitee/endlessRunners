package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
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

    @org.junit.jupiter.api.Test
    fun buttonStateChangeSerializationTest() {
        val gameState = DummyObjects.createDummyGameState()

        // test press action
        var buttonChange = gameState.buttons[0].press
        var stringRep = buttonChange.toString()

        var readButtonChange = GameButton.StateChange.fromString(gameState, stringRep)
        assertNotNull(readButtonChange)
        assertEquals(0, readButtonChange!!.gameButton.index)
        assertEquals(GameButton.InteractionType.PRESS, readButtonChange.interactionType)

        // test release action
        buttonChange = gameState.buttons[1].release
        stringRep = buttonChange.toString()

        readButtonChange = GameButton.StateChange.fromString(gameState, stringRep)
        assertNotNull(readButtonChange)
        assertEquals(1, readButtonChange!!.gameButton.index)
        assertEquals(GameButton.InteractionType.RELEASE, readButtonChange.interactionType)
    }
}
