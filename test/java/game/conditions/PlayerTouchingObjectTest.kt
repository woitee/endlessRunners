package game.conditions

import game.BlockHeight
import game.BlockWidth
import game.DummyObjects
import game.objects.GameObjectClass
import game.objects.SolidBlock
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

        assertTrue(downCondition.isTrue(gameState))
        gameState.player.x = (BlockWidth * 2 + 10).toDouble()
        assertTrue(downCondition.isTrue(gameState))

        gameState.player.y += 1
        assertFalse(downCondition.isTrue(gameState))
    }
}