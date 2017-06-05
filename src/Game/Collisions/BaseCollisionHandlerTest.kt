package Game.Collisions

import Game.GameObjects.SolidBlock
import Geom.Direction4
import createGame
import org.junit.jupiter.api.Assertions.*

/**
 * Created by woitee on 05/06/2017.
 */
internal class BaseCollisionHandlerTest {
    @org.junit.jupiter.api.Test
    fun nearestCollision() {
        val game = createGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 2, 1)

        // Direct Right Collision
        var coll = game.collHandler.nearestCollision(game.gameState, 12.0, 36.0, 36.0, 36.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        assertEquals(24.0, coll.locationX)
        assertEquals(36.0, coll.locationY)
        assertEquals(12.0, coll.myLocationX)
        assertEquals(36.0, coll.myLocationY)
        assertEquals(Direction4.RIGHT, coll.direction)

        // Angled Up Right Collision
        coll = game.collHandler.nearestCollision(game.gameState, 0.0, 24.0, 96.0, 48.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        assertEquals(24.0, coll.locationX)
        assertEquals(30.0, coll.locationY)
        assertEquals(0.0, coll.myLocationX)
        assertEquals(24.0, coll.myLocationY)
        assertEquals(Direction4.RIGHT, coll.direction)

        // Angled Collision from Bottom
        coll = game.collHandler.nearestCollision(game.gameState, 36.0, 0.0, 60.0, 72.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        assertEquals(44.0, coll.locationX)
        assertEquals(24.0, coll.locationY)
        assertEquals(Direction4.UP, coll.direction)

        // Very sharply Angled Collision from Top
        coll = game.collHandler.nearestCollision(game.gameState, 0.0, 56.0, 48.0, 47.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        assertEquals(42.0 + 2.0/3.0, coll.locationX)
        assertEquals(48.0, coll.locationY)
        assertEquals(Direction4.DOWN, coll.direction)

        // Coll from Left, just glancing the corner
        coll = game.collHandler.nearestCollision(game.gameState, 84.0, 36.0, 59.0, 12.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[2, 1]!!, coll!!.other)
        assertEquals(72.0, coll.locationX)
        assertEquals(24.48, coll.locationY)
        assertEquals(Direction4.LEFT, coll.direction)

        println("Tested all nearestCollision")

    }
}