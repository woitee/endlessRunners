package cz.woitee.endlessRunners

import cz.woitee.game.Game
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.descriptions.CrouchGameDescription
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.playerControllers.KeyboardPlayerController
import cz.woitee.game.playerControllers.RecordingWrapper
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
    val gameDescription = CrouchGameDescription()
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()

    val levelGenerator = DelayedTwinDFSLevelGenerator(0.25, SimpleLevelGenerator())
    val playerController = RecordingWrapper(KeyboardPlayerController())

    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )

    game.gameState.tag = "Main"
    game.run()

    playerController.saveToFile("Recording" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
}