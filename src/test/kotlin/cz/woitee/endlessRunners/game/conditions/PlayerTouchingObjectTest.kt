package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.DummyObjects
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.geom.Direction4
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
