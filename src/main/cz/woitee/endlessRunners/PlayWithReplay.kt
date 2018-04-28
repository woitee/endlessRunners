package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController
import cz.woitee.endlessRunners.game.playerControllers.RecordingWrapper
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