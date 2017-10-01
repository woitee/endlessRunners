package utils

import cz.woitee.game.DummyObjects
import cz.woitee.game.HeightBlocks
import cz.woitee.game.WidthBlocks
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.Player
import cz.woitee.game.objects.SolidBlock
import cz.woitee.utils.CopyUtils
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
    fun copyGameStateBySerialization() {
        innerCopyGameStateTest(true)
    }

    @org.junit.jupiter.api.Test
    fun copyGameStateDirectly() {
        innerCopyGameStateTest(false)
    }

    fun innerCopyGameStateTest(bySerialization: Boolean) {
        val gameState = DummyObjects.createDummyGameState()
        val solidBlock = SolidBlock()
        gameState.addToGrid(solidBlock, 1, 2)
        gameState.player.x = 1.5

        val copy = if (bySerialization) {CopyUtils.copyBySerialization(gameState)} else {gameState.makeCopy()}
        assertEquals(gameState.gameObjects.count(), copy.gameObjects.count())

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
        assertNotNull(gameState.grid[1, 2])

        val copiedBlock = copy.grid[1, 2]!!
        assertNotEquals(solidBlock, copiedBlock)
        assertEquals(solidBlock.gameObjectClass, copiedBlock.gameObjectClass)
        assertEquals(solidBlock.x, copiedBlock.x)
        assertEquals(solidBlock.y, copiedBlock.y)
        for (x in 0 until WidthBlocks) {
            for (y in 0 until HeightBlocks) {
                assertNotEquals(GameObjectClass.PLAYER, gameState.grid[x, y]?.gameObjectClass)
            }
        }
    }
}