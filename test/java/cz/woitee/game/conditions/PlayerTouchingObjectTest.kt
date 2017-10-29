package cz.woitee.game.conditions

import cz.woitee.game.BlockHeight
import cz.woitee.game.BlockWidth
import cz.woitee.game.DummyObjects
import cz.woitee.game.conditions.PlayerTouchingObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.geom.Direction4
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