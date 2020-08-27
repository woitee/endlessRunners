package cz.woitee.endlessRunners.experiments

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.playerControllers.*
import cz.woitee.endlessRunners.game.playerControllers.wrappers.RecordingWrapper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Game recording runs of several AI players in the experiment runs.
 */
fun main() {
    runGameWithController(DFSPlayerController(BasicDFS()), "basicDFS", 1)
    runGameWithController(DFSPlayerController(BasicDFS()), "basicDFS", 2)
    runGameWithController(DFSPlayerController(DelayedTwinDFS(0.25)), "delayedTwinDFS", 1)
    runGameWithController(DFSPlayerController(DelayedTwinDFS(0.25)), "delayedTwinDFS", 2)
    runGameWithController(HoldingOneButtonPlayerController(0), "keepJumping", 1)
    runGameWithController(HoldingOneButtonPlayerController(0), "keepJumping", 2)
    runGameWithController(NoActionPlayerController(), "noAction", 1)
    runGameWithController(NoActionPlayerController(), "noAction", 2)
}

fun runGameWithController(playerController: PlayerController, identifier: String, gameNumber: Int) {
    val gameDescription = CrouchGameDescription()
    val visualiser = GamePanelVisualizer("Baseline Run")

    val levelGenerator = getLevelGeneratorForGame(gameNumber)
    val actualPlayerController = RecordingWrapper(playerController)

    val game = Game(
            levelGenerator, actualPlayerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription,
            freezeOnStartSeconds = 1.0
    )

    game.run((5 * 60 * 1000).toLong())

    actualPlayerController.saveToFile("RecordingGame${gameNumber}_${identifier}_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
}
