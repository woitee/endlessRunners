package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.DummyObjects
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import org.junit.jupiter.api.Assertions.*
import kotlin.math.abs

class EvoGameTest {
    @org.junit.jupiter.api.Test
    fun testRoundingPlayerSpeed() {
        for (playerSpeed in EvolvedGameDescription.possibleSpeeds) {
            println(playerSpeed)
            val gameDescription = DummyObjects.MockGameDescription()
            gameDescription.playerStartingSpeed = playerSpeed

            val game = Game(
                FlatLevelGenerator(),
                NoActionPlayerController(),
                null,
                gameDescription = gameDescription
            )
            game.init()
            val player = game.gameState.player
            val startX = player.x

            val playerXs = ArrayList<Double>()
            while (player.x - startX < 48) {
                game.update()
                playerXs.add(player.x - startX)
            }
            game.update()
            playerXs.add(player.x - startX)
            println("speed is ${playerXs[1] - playerXs[0]} px, ${player.xspeed} blocks")

            val epsilon = 0.0001
            val target = 2.0 * BlockWidth
            val closest = playerXs.minByOrNull { abs(target - it) }!!

            assertEquals(target, closest, epsilon)
        }
    }
}
