package game.algorithms

import game.Game
import game.descriptions.BitTripGameDescription
import game.levelGenerators.DFSEnsuringGenerator
import game.levelGenerators.SimpleLevelGenerator
import game.playerControllers.DFSPlayerController
import java.io.File
import java.io.ObjectInputStream

internal class DFSTest {
    @org.junit.jupiter.api.Test
    fun bugWrongCollisionWhenFarIntoTheGame() {
        runTestFromFile("test/data/GameState_2017_09_03-14_52_43.dmp")
    }

    @org.junit.jupiter.api.Test
    fun bugAfterAddingCustomBlocks() {
        runTestFromFile("test/data/GameState_2017_09_21-22_11_14.dmp")
    }

    private fun runTestFromFile(filePath: String) {
        val game = Game(
                DFSEnsuringGenerator(SimpleLevelGenerator()),
                DFSPlayerController(),
                null,
                mode = Game.Mode.INTERACTIVE,
                gameDescription = BitTripGameDescription(),
                restartOnGameOver = false
        )

        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.readObject(ois)

        game.run()
    }
}