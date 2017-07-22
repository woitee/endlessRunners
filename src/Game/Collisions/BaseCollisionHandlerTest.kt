package Game.Collisions

import Game.Game
import Game.GameObjects.SolidBlock
import Geom.Direction4
import createGame
import org.junit.jupiter.api.Assertions

/**
 * Created by woitee on 22/07/2017.
 */
class BaseCollisionHandlerTest {
    @org.junit.jupiter.api.Test
    fun theoreticalCollisions() {
        val game = createGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 2, 1)

        // Direct Right Collision
        var coll = game.collHandler.nearestCollision(game.gameState, 12.0, 36.0, 36.0, 36.0)
        Assertions.assertNotNull(coll)
        Assertions.assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        Assertions.assertEquals(24.0, coll.locationX)
        Assertions.assertEquals(36.0, coll.locationY)
        Assertions.assertEquals(12.0, coll.myLocationX)
        Assertions.assertEquals(36.0, coll.myLocationY)
        Assertions.assertEquals(Direction4.RIGHT, coll.direction)

        // Angled Up Right Collision
        coll = game.collHandler.nearestCollision(game.gameState, 0.0, 24.0, 96.0, 48.0)
        Assertions.assertNotNull(coll)
        Assertions.assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        Assertions.assertEquals(24.0, coll.locationX)
        Assertions.assertEquals(30.0, coll.locationY)
        Assertions.assertEquals(0.0, coll.myLocationX)
        Assertions.assertEquals(24.0, coll.myLocationY)
        Assertions.assertEquals(Direction4.RIGHT, coll.direction)

        // Angled Collision from Bottom
        coll = game.collHandler.nearestCollision(game.gameState, 36.0, 0.0, 60.0, 72.0)
        Assertions.assertNotNull(coll)
        Assertions.assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        Assertions.assertEquals(44.0, coll.locationX)
        Assertions.assertEquals(24.0, coll.locationY)
        Assertions.assertEquals(Direction4.UP, coll.direction)

        // Very sharply Angled Collision from Top
        coll = game.collHandler.nearestCollision(game.gameState, 0.0, 56.0, 48.0, 47.0)
        Assertions.assertNotNull(coll)
        Assertions.assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        Assertions.assertEquals(42.0 + 2.0 / 3.0, coll.locationX)
        Assertions.assertEquals(48.0, coll.locationY)
        Assertions.assertEquals(Direction4.DOWN, coll.direction)

        // Coll from Left, just glancing the corner
        coll = game.collHandler.nearestCollision(game.gameState, 84.0, 36.0, 59.0, 12.0)
        Assertions.assertNotNull(coll)
        Assertions.assertEquals(game.gameState.grid[2, 1]!!, coll!!.other)
        Assertions.assertEquals(72.0, coll.locationX)
        Assertions.assertEquals(24.48, coll.locationY)
        Assertions.assertEquals(Direction4.LEFT, coll.direction)

        // Test not colliding with the line being completely off
        coll = game.collHandler.nearestCollision(game.gameState, 12.0, 60.0, 36.0, 61.0)
        Assertions.assertNull(coll)

        // Test not collision moving on wall upwards
//        coll = game.collHandler.nearestCollision(game.gameState, 22.0, 24.0, 26.0, 24.0)
//        assertNull(coll)
    }

    @org.junit.jupiter.api.Test
    fun movingOnSurface() {
        val game = createGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 2, 1)

        // Moving on top of blocks
        val coll = game.collHandler.nearestCollision(game.gameState, 12.0, 48.0, 60.0, 48.0)
        Assertions.assertNull(coll)
    }

    @org.junit.jupiter.api.Test
    fun hittingAWall() {
        val game = createGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 1, 2)

        // Hitting a wall directly in between two blocks
        val coll = game.collHandler.nearestCollision(game.gameState, 12.0, 48.0, 36.0, 48.0)
        Assertions.assertNotNull(coll)
    }
}