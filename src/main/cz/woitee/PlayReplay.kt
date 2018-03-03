package cz.woitee

import cz.woitee.game.Game
import cz.woitee.game.descriptions.CrouchGameDescription
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.FromRecordingLevelGenerator
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.game.playerControllers.FromRecordingPlayerController
import cz.woitee.game.playerControllers.KeyboardPlayerController
import cz.woitee.game.playerControllers.RecordingWrapper
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
    val replayPath: String = "Recording2018_03_03-23_14_29.dmp"
    val gameDescription = CrouchGameDescription()
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()

    val levelGenerator = FromRecordingLevelGenerator(replayPath)
    val playerController = FromRecordingPlayerController(replayPath)

    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )

    game.gameState.tag = "Main"
    game.run()
}