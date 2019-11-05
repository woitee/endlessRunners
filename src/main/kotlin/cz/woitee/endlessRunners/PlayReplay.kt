package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.FromRecordingLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.FromRecordingPlayerController
import java.util.*

/** Plays a replay from a log file (sent e.g. in the experiment) */
fun main(args: Array<String>) {
    val replayPath: String = "RecordingGame12018_03_05-17_53_51.dmp"
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
