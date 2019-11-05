package cz.woitee.endlessRunners.utils

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.ButtonModel
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.objects.SolidBlock
import org.junit.jupiter.api.Assertions.*

internal class CopyUtilsTest {
    @org.junit.jupiter.api.Test
    fun copyPlayer() {
        val player = Player(1.5, 1.5)
        player.xspeed = 1.6
        player.yspeed = 1.6
        player.gameState = DummyObjects.createDummyGameState()

        val playerCopy = Player(0.0, 0.0)
        playerCopy.gameState = player.gameState
        CopyUtils.copyBySerialization(player, playerCopy)
        assertEquals(player.x, playerCopy.x)
        assertEquals(player.y, playerCopy.y)
        assertEquals(player.xspeed, playerCopy.yspeed)
        assertEquals(player.yspeed, playerCopy.yspeed)
        assertNotEquals(player, playerCopy)
    }

    @org.junit.jupiter.api.Test
    fun copyGameStateBySerialization() {
        innerCopyGameStateTest(true)
    }

    @org.junit.jupiter.api.Test
    fun copyGameStateDirectly() {
        innerCopyGameStateTest(false)
    }

    fun innerCopyGameStateTest(bySerialization: Boolean) {
        val gameState = DummyObjects.createDummyGameState()
        gameState.advance(1 / 75.0)
        gameState.allActions[1].applyOn(gameState)
        gameState.advance(1 / 75.0)
        gameState.isGameOver = true

        val solidBlock = SolidBlock()
        gameState.addToGrid(solidBlock, 1, 2)
        gameState.player.x = 1.5

        val copy = if (bySerialization) {
            CopyUtils.copyBySerialization(gameState, GameState(gameState.game, null))
        } else {
            gameState.makeCopy()
        }

        assertSimiliarGameState(gameState, copy)
    }

    fun assertSimiliarGameState(gameState: GameState, copy: GameState) {
        assertEquals(gameState.gameObjects.count(), copy.gameObjects.count())
        assertEquals(gameState.gridX, copy.gridX)
        assertEquals(gameState.gameTime, copy.gameTime)
        assertEquals(gameState.isGameOver, copy.isGameOver)

        assertEquals(gameState.buttons.count(), copy.buttons.count())
        for (i in gameState.buttons.indices) {
            assertEquals(gameState.buttons[i], copy.buttons[i])
            assertEquals(gameState.buttons[i].isPressed, copy.buttons[i].isPressed)
            assertEquals(gameState.buttons[i].index, copy.buttons[i].index)
            assertEquals(gameState.buttons[i].pressedGameTime, copy.buttons[i].pressedGameTime)
        }

        assertNotEquals(gameState.player, copy.player)
        assertEquals(gameState.player.x, copy.player.x)
        for (gameObject in copy.gameObjects) {
            if (gameObject.gameObjectClass == GameObjectClass.PLAYER) {
                assertEquals(copy.player, gameObject)
            }
        }
        for (gameObject in copy.updateObjects) {
            if (gameObject.gameObjectClass == GameObjectClass.PLAYER) {
                assertEquals(copy.player, gameObject)
            }
        }

        val testObject: GameObject? = gameState.gameObjects.firstOrNull { it.gameObjectClass != GameObjectClass.PLAYER }
        if (testObject != null) {
            val copiedObject = copy.grid[copy.gridLocation(testObject.location)]
            assertNotNull(copiedObject)

            assertNotSame(testObject, copiedObject)
            assertEquals(testObject.gameObjectClass, copiedObject!!.gameObjectClass)
            assertEquals(testObject.x, copiedObject.x)
            assertEquals(testObject.y, copiedObject.y)
        }

        for (x in 0 until WidthBlocks) {
            for (y in 0 until HeightBlocks) {
                assertNotEquals(GameObjectClass.PLAYER, copy.grid[x, y]?.gameObjectClass)
            }
        }
    }

    @org.junit.jupiter.api.Test
    fun copyButtonModel() {
        val firstState = DummyObjects.createDummyGameState()
        val secondState = firstState.makeCopy()
        val buttonModel = ButtonModel(firstState, secondState, 0.1)
        buttonModel.press(0)

        val modelCopy = ButtonModel(GameState(firstState.game, null), GameState(secondState.game, null), 0.1)
        CopyUtils.copyBySerialization(buttonModel, modelCopy)

        assertSimiliarButtonModel(buttonModel, modelCopy)
    }

    fun assertSimiliarButtonModel(buttonModel: ButtonModel, modelCopy: ButtonModel) {
        assertNotEquals(buttonModel, modelCopy)
        assertNotEquals(buttonModel.currentState, modelCopy.currentState)
        assertNotEquals(buttonModel.delayedState, modelCopy.delayedState)
        assertNotEquals(modelCopy.currentState, modelCopy.delayedState)
        assertSimiliarGameState(buttonModel.currentState, modelCopy.currentState)
        assertSimiliarGameState(buttonModel.delayedState, modelCopy.delayedState)

        assertEquals(buttonModel.maxButton, modelCopy.maxButton)
    }

    @org.junit.jupiter.api.Test
    fun copyDelayedTwinDFS() {
        val gameState = DummyObjects.createDummyGameState()
        val delayedTwinDFS = DelayedTwinDFS(0.25)
        delayedTwinDFS.init(gameState)
        delayedTwinDFS.searchForAction(gameState)

        assertTrue(delayedTwinDFS.lastStats.success)

        val stateCopy = DummyObjects.createDummyGameState()
        val delayedTwinCopy = DelayedTwinDFS(delayedTwinDFS.delayTime)
        delayedTwinCopy.init(stateCopy)
        CopyUtils.copyBySerialization(delayedTwinDFS, delayedTwinCopy)

        assertNotEquals(delayedTwinDFS, delayedTwinCopy)
        assertSimiliarButtonModel(delayedTwinDFS.buttonModel, delayedTwinCopy.buttonModel)
    }

    @org.junit.jupiter.api.Test
    fun copyGameDescription() {
        innerCopyGameDescriptionTest(GameDescription())
        innerCopyGameDescriptionTest(CrouchGameDescription())
        innerCopyGameDescriptionTest(BitTriGameDescription())
    }

    fun innerCopyGameDescriptionTest(gameDescription: GameDescription) {
        val copy = gameDescription.makeCopy()

        assertNotSame(gameDescription, copy)

        assertEquals(gameDescription.javaClass, copy.javaClass)
        assertEquals(gameDescription.allActions.count(), copy.allActions.count())
        for (i in 0 until gameDescription.allActions.count()) {
            val action = gameDescription.allActions[i]
            val copiedAction = copy.allActions[i]
            assertEquals(action.javaClass, copiedAction.javaClass)
            assertNotSame(action, copiedAction)
        }
        assertEquals(gameDescription.allObjects.count(), copy.allObjects.count())
        for (i in 0 until gameDescription.allObjects.count()) {
            val gameObjects = gameDescription.allObjects[i]
            val copiedObject = copy.allObjects[i]
            assertEquals(gameObjects.javaClass, copiedObject.javaClass)
            assertNotSame(gameObjects, copiedObject)
        }
        assertEquals(gameDescription.playerStartingSpeed, copy.playerStartingSpeed)
    }
}
