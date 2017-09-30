package game.algorithms

import cz.woitee.game.Game
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.levelGenerators.DFSEnsuringGenerator
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.gui.GamePanelVisualizer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.ObjectInputStream

internal class DFSTest {
    @org.junit.jupiter.api.Test
    fun bugWrongCollisionWhenFarIntoTheGame() {
        runTestFromFile("cz.woitee.test/data/GameState_2017_09_03-14_52_43.dmp", expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugAfterAddingCustomBlocks() {
        runTestFromFile("cz.woitee.test/data/GameState_2017_09_21-22_11_14.dmp", expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugOfSimultaneousActions() {
        runTestFromFile("cz.woitee.test/data/GameState_2017_09_23-17_40_36.dmp")
    }

    @org.junit.jupiter.api.Test
    fun pertainingTest() {
        runTestFromFile("cz.woitee.test/data/GameState_2017_09_28-16_07_19.dmp")
    }

    private fun runTestFromFile(filePath: String, expectGameOver: Boolean = false, time: Double = 2.0) {
        val game = Game(
                DFSEnsuringGenerator(SimpleLevelGenerator()),
                DFSPlayerController(),

//                null,
                GamePanelVisualizer(),

                mode = Game.Mode.INTERACTIVE,
                gameDescription = BitTripGameDescription(),
                restartOnGameOver = false
        )

        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.readObject(ois)

        game.start()
        game.updateThread.join((time * 1000).toLong())
        game.stop()
        assertEquals(expectGameOver, game.gameState.isGameOver)
    }
}