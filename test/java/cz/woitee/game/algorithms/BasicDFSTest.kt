package cz.woitee.endlessRunners.game.algorithms

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.AbstractDFS
import cz.woitee.endlessRunners.game.descriptions.BitTripGameDescription
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.ObjectInputStream

internal class BasicDFSTest {
    @org.junit.jupiter.api.Test
    fun bugWrongCollisionWhenFarIntoTheGame() {
        runTestFromFile("test/data/GameState_2017_09_03-14_52_43.dmp", 1, expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugAfterAddingCustomBlocks() {
        runTestFromFile("test/data/GameState_2017_09_21-22_11_14.dmp", 1, expectGameOver = true)
    }

    @org.junit.jupiter.api.Test
    fun bugOfSimultaneousActions() {
        runTestFromFile("test/data/GameState_2017_09_23-17_40_36.dmp", 1)
    }

    @org.junit.jupiter.api.Test
    fun pertainingTest() {
        runTestFromFile("test/data/GameState_2017_09_28-16_07_19.dmp", 1)
    }

    internal fun runTestFromFile(filePath: String, serializationVersion: Int, expectGameOver: Boolean = false, time: Double = 2.0,
                                 dfsProvider: AbstractDFS = BasicDFS(), gameDescription: GameDescription = BitTripGameDescription()) {
        val game = Game(
                FlatLevelGenerator(),
                DFSPlayerController(dfsProvider),

//                null,
                GamePanelVisualizer(),
                updateRate = 75.0,

                mode = Game.Mode.INTERACTIVE,
                gameDescription = gameDescription,
                restartOnGameOver = false
        )
        game.gameState.serializationVersion = serializationVersion
        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.readObject(ois)

        //TODO remove this
//        // UNCOMMENT to remove removable blocks
//        val toRemove = ArrayList<GameObject>()
//        for (gameObject in game.gameState.gameObjects) {
//            if (gameObject.dumpChar == '1')
//                toRemove.add(gameObject)
//        }
//        for (gameObject in toRemove) {
//            game.gameState.remove(gameObject)
//        }

        // Moving player right or left
//        game.gameState.player.x -= 3
//        game.gameState.addColumn(LevelGenerationUtils.generateColumnFromString("######", game.gameState))
//        DebugUtils.printDebugInfo(game.gameState)

        var exception: Throwable? = null
            game.updateThread.thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e -> exception = e }

        game.start()

//         Warning! The time on which this joins will kill live debugging
        game.updateThread.join((time * 1000).toLong())
//        game.updateThread.join()

        game.stop()
        assertEquals(expectGameOver, game.gameState.isGameOver, if (expectGameOver) "A gameover was expected, but didn't happen" else "A gameover happened, but was not expected")
        if (exception != null) {
            throw exception!!
        }
    }
}