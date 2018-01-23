package cz.woitee.game.collisions

import cz.woitee.game.BlockWidth
import cz.woitee.game.objects.SolidBlock
import cz.woitee.geom.Direction4
import cz.woitee.game.DummyObjects
import cz.woitee.game.GameWidth
import org.junit.jupiter.api.Assertions.*
import cz.woitee.game.levelGenerators.BlockLevelGenerator.Block

/**
 * Created by woitee on 22/07/2017.
 */
class BaseCollisionHandlerTest {
    @org.junit.jupiter.api.Test
    fun theoreticalCollisions() {
        val game = DummyObjects.createDummyGame()
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
        assertEquals(42.0 + 2.0 / 3.0, coll.locationX)
        assertEquals(48.0, coll.locationY)
        assertEquals(Direction4.DOWN, coll.direction)

        // Coll from Left, just glancing the corner
        coll = game.collHandler.nearestCollision(game.gameState, 84.0, 36.0, 59.0, 12.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[2, 1]!!, coll!!.other)
        assertEquals(72.0, coll.locationX)
        assertEquals(24.48, coll.locationY)
        assertEquals(Direction4.LEFT, coll.direction)

        // Test not colliding with the line being completely off
        coll = game.collHandler.nearestCollision(game.gameState, 12.0, 60.0, 36.0, 61.0)
        assertNull(coll)

        // Test not collision moving on wall upwards
//        coll = game.collHandler.nearestCollision(game.currentState, 22.0, 24.0, 26.0, 24.0)
//        assertNull(coll)
    }

    @org.junit.jupiter.api.Test
    fun movingOnSurface() {
        val game = DummyObjects.createDummyGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 2, 1)

        // Moving on top of blocks
        val coll = game.collHandler.nearestCollision(game.gameState, 12.0, 48.0, 60.0, 48.0)
        assertNull(coll)
    }

    @org.junit.jupiter.api.Test
    fun hittingAWall() {
        val game = DummyObjects.createDummyGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 1, 2)

        // Hitting a wall directly in between two blocks
        val coll = game.collHandler.nearestCollision(game.gameState, 12.0, 48.0, 36.0, 48.0)
        assertNotNull(coll)
    }

    @org.junit.jupiter.api.Test
    fun collisionOnShiftedGrid() {
        val game = DummyObjects.createDummyGame()
        game.gameState.gameObjects.clear()
        game.gameState.gridX = 100
        game.gameState.addToGrid(SolidBlock(), 1, 1)

        // Direct Right Collision
        val coll = game.collHandler.nearestCollision(game.gameState, 2412.0, 36.0, 2436.0, 36.0)
        assertNotNull(coll)
        assertEquals(game.gameState.grid[1, 1]!!, coll!!.other)
        assertEquals(2424.0, coll.locationX)
        assertEquals(36.0, coll.locationY)
        assertEquals(2412.0, coll.myLocationX)
        assertEquals(36.0, coll.myLocationY)
        assertEquals(Direction4.RIGHT, coll.direction)
    }

    @org.junit.jupiter.api.Test
    fun weirdlyAllignedCollision() {
        val game = DummyObjects.createDummyGame()
        game.gameState.gridX = 134207
        val block = Block(game.gameDescription, arrayListOf(
            "                    #         ",
            "                 #            ",
            "   #    #      #    ##     ## ",
            "             #################",
            "        ######################",
            "     #########################",
            " #############################"
        ))
        for (y in 0 until block.height) {
            for (x in 0 until block.width) {
                game.gameState.addToGrid(block.definition[x, y], x, y + 1)
            }
        }
        val player = game.gameState.player
        player.x = 3221135.7599763623
        player.y = 120.5759999999999
        player.xspeed = 12.0
        player.yspeed = -9.73333333333334

        val coll = game.collHandler.getCollision(game.gameState.player)
        assertNotNull(coll)
        assertEquals(coll!!.direction, Direction4.RIGHT)
    }

    @org.junit.jupiter.api.Test
    fun notCrashingOnOutOfGridCollisions() {
        val game = DummyObjects.createDummyGame()
        game.gameState.gameObjects.clear()
        game.gameState.addToGrid(SolidBlock(), 1, 1)
        game.gameState.addToGrid(SolidBlock(), 2, 1)

        val borderX = game.gameState.grid.width * BlockWidth
        val coll = game.collHandler.nearestCollision(game.gameState, borderX - 10.0, 1.0, borderX + 10.0, 1.0)
        assertNull(coll)
    }
}