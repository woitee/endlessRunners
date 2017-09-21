package game.algorithms

import game.Game
import game.gameDescriptions.BitTripGameDescription
import game.levelGenerators.DFSEnsuringGenerator
import game.levelGenerators.SimpleLevelGenerator
import game.playerControllers.DFSPlayerController
import java.io.File
import java.io.ObjectInputStream

internal class DFSTest {
    @org.junit.jupiter.api.Test
    fun loadSaved() {
        val game = Game(
            DFSEnsuringGenerator(SimpleLevelGenerator()),
            DFSPlayerController(),
            null,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = BitTripGameDescription(),
            restartOnGameOver = false
        )

        val filePath = "out/states/GameState_2017_09_03-14_52_43.dmp"
        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.readObject(ois)

        game.run()
    }
}