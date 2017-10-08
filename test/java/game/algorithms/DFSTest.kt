package game.algorithms

import cz.woitee.game.Game
import cz.woitee.game.algorithms.DFS
import cz.woitee.game.algorithms.DFSBase
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.descriptions.GameDescription
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.gui.GamePanelVisualizer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.ObjectInputStream

internal class DFSTest {
    @org.junit.jupiter.api.Test
    fun bugWrongCollisionWhenFarIntoTheGame() {
        runTestFromFile("test/data/GameState_2017_09_03-14_52_43.dmp", expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugAfterAddingCustomBlocks() {
        runTestFromFile("test/data/GameState_2017_09_21-22_11_14.dmp", expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugOfSimultaneousActions() {
        runTestFromFile("test/data/GameState_2017_09_23-17_40_36.dmp")
    }

    @org.junit.jupiter.api.Test
    fun pertainingTest() {
        runTestFromFile("test/data/GameState_2017_09_28-16_07_19.dmp")
    }

    internal fun runTestFromFile(filePath: String, expectGameOver: Boolean = false, time: Double = 2.0,
                                 dfsProvider: DFSBase = DFS(debug = true), gameDescription: GameDescription = BitTripGameDescription()) {
        val game = Game(
                SimpleLevelGenerator(),
                DFSPlayerController(dfsProvider),

//                null,
                GamePanelVisualizer(),

                mode = Game.Mode.INTERACTIVE,
                gameDescription = gameDescription,
                restartOnGameOver = false
        )

        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.readObject(ois)

        var exception: Throwable? = null
            game.updateThread.thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e -> exception = e }

        game.start()
        // Warning! The time on which this joins will kill live debugging
        game.updateThread.join((time * 1000).toLong())
//        game.updateThread.join()
        game.stop()
        assertEquals(expectGameOver, game.gameState.isGameOver)
        if (exception != null) {
            throw exception!!
        }
    }
}