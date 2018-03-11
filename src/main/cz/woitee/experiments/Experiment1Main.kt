package cz.woitee.experiments

import cz.woitee.game.Game
import cz.woitee.game.descriptions.CrouchGameDescription
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.game.playerControllers.KeyboardPlayerController
import cz.woitee.game.playerControllers.RecordingWrapper
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
    val gui = ExperimentGUI(
        arrayOf("Reakční test", "Hra 1", "Hra 2"),
        arrayOf(
            {
                println("Reaction Test clicked")
                ReactionAndPrecisionTest().run()
            }, {
                println("Game 1 Start")
                runGame1()
            }, {
                println("Game 2 Start")
                runGame2()
            }
        ),
        arrayOf("ReactionTest_.*log", "RecordingGame1_.*dmp", "RecordingGame2_.*dmp")
    )
    gui.show()
}

fun runGame1(timeMinutes: Double = 5.0) {
    val gamePreparation = IntermediatoryDescriptorFrame("")
    gamePreparation.waitUntillInteraction()

    val gameDescription = CrouchGameDescription()
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()

    val levelGenerator = DelayedTwinDFSLevelGenerator(0.25, SimpleLevelGenerator())
    val playerController = RecordingWrapper(KeyboardPlayerController())

    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )

    game.gameState.tag = "Hra 1"

    game.run((timeMinutes * 60 * 1000).toLong())

    if (!game.endedFromVisualizer)
        playerController.saveToFile("RecordingGame1_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
}

fun runGame2(timeMinutes: Double = 5.0) {
    val gameDescription = CrouchGameDescription()
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()

    val levelGenerator = DelayedTwinDFSLevelGenerator(0.25, SimpleLevelGenerator())
    val playerController = RecordingWrapper(KeyboardPlayerController())

    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )

    game.gameState.tag = "Hra 2"
    game.run((timeMinutes * 60 * 1000).toLong())

    if (!game.endedFromVisualizer)
        playerController.saveToFile("RecordingGame2_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
}

fun runGame() {
}