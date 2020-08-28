package cz.woitee.endlessRunners.utils

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.DFSImpossibleGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.ColumnCopyingLevelGenerator
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import org.junit.jupiter.api.Assertions.*
import java.lang.Thread.sleep

class ComputationStopperTest {
    @org.junit.jupiter.api.Test
    fun stopBasicDFS() {
        performTestWith(DFSPlayerController(BasicDFS()))
    }

    @org.junit.jupiter.api.Test
    fun stopDelayedTwinDFS() {
        performTestWith(DFSPlayerController(DelayedTwinDFS(0.1)))
    }

    fun performTestWith(playerController: PlayerController) {
        val game = getImpossibleGame(playerController)

        val dfsPlayerController = game.playerController as DFSPlayerController
        Thread {
            sleep(5000)
            println("Stop now!")
            dfsPlayerController.dfs.computationStopper.stop()
        }.start()

        game.run(2000)

        // The only test is that this function finishes
        assertEquals(true, true)
    }

    fun getImpossibleGame(playerController: PlayerController): Game {
        val game = Game(
            ColumnCopyingLevelGenerator(arrayList(HeightBlocks) { SolidBlock() }),
            playerController,
            GamePanelVisualizer(),
            gameDescription = DFSImpossibleGameDescription()
        )

        return game
    }
}
