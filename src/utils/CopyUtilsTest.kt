package utils

import game.DummyObjects
import game.Game
import game.GameState
import game.gameObjects.Player
import game.gameObjects.SolidBlock
import org.junit.jupiter.api.Assertions.*

internal class CopyUtilsTest {
    @org.junit.jupiter.api.Test
    fun copyPlayer() {
        val player = Player()
        player.x = 1.5
        player.y = 1.5
        player.xspeed = 1.5
        player.yspeed = 1.5
        player.gameState = DummyObjects.createDummyGameState()

        val playerCopy = CopyUtils.copyBySerialization(player)
        assertEquals(player.x, playerCopy.x)
        assertEquals(player.y, playerCopy.y)
        assertEquals(player.xspeed, playerCopy.yspeed)
        assertEquals(player.yspeed, playerCopy.yspeed)
        assertThrows(UninitializedPropertyAccessException::class.java, { playerCopy.gameState })
        assertNotEquals(player, playerCopy)
    }

    @org.junit.jupiter.api.Test
    fun copyGameState() {
        val gameState = DummyObjects.createDummyGameState()
        val solidBlock = SolidBlock()
        gameState.addToGrid(solidBlock, 1, 2)
        gameState.player.x = 1.5

        val copy = CopyUtils.copyBySerialization(gameState)
        assertEquals(gameState.gameObjects.count(), copy.gameObjects.count())

        assertNotEquals(gameState.player, copy.player)
        assertEquals(gameState.player.x, copy.player.x)
        assertNotNull(gameState.grid[1, 2])

        val copiedBlock = copy.grid[1, 2]!!
        assertNotEquals(solidBlock, copiedBlock)
        assertEquals(solidBlock.gameObjectClass, copiedBlock.gameObjectClass)
        assertEquals(solidBlock.x, copiedBlock.x)
        assertEquals(solidBlock.y, copiedBlock.y)
    }
}