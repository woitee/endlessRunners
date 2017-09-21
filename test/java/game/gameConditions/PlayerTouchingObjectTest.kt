package game.gameConditions

import game.BlockHeight
import game.BlockWidth
import game.DummyObjects
import game.gameObjects.GameObjectClass
import game.gameObjects.SolidBlock
import geom.Direction4
import org.junit.jupiter.api.Assertions.*

internal class PlayerTouchingObjectTest {
    @org.junit.jupiter.api.Test
    fun touchingObjectDown() {
        val gameState = DummyObjects.createDummyGameState()
        gameState.grid[2, 2] = SolidBlock()

        gameState.player.x = (BlockWidth * 2 - 10).toDouble()
        gameState.player.y = (BlockHeight * 3).toDouble()

        val downCondition = PlayerTouchingObject(Direction4.DOWN, GameObjectClass.SOLIDBLOCK)

        assertTrue(downCondition.holds(gameState))
        gameState.player.x = (BlockWidth * 2 + 10).toDouble()
        assertTrue(downCondition.holds(gameState))

        gameState.player.y += 1
        assertFalse(downCondition.holds(gameState))
    }
}