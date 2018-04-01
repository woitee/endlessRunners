package cz.woitee.endlessRunners.experiments

import cz.woitee.game.Game
import cz.woitee.game.algorithms.dfs.BasicDFS
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.descriptions.CrouchGameDescription
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DeterministicSeeds
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.playerControllers.KeyboardPlayerController
import cz.woitee.game.playerControllers.RecordingWrapper
import java.text.SimpleDateFormat
import java.util.*

val startsWithGame1: Boolean = Random().nextBoolean()

fun main(args: Array<String>) {
    println("Starting with game " + if (startsWithGame1) 1 else 2)
    val gui = ExperimentGUI(
        arrayOf("Reakční test", "Demonstrace", "Hra 1", "Hra 2"),
        arrayOf(
            {
                println("Reaction Test clicked")
                ReactionAndPrecisionTest().run()
                true
            }, {
                println("Demonstration")
                runDemo()
            }, {
                println("Game 1 Start")
                runGame1()
            }, {
                println("Game 2 Start")
                runGame2()
            }
        ),
        if (startsWithGame1) {
            arrayOf("ReactionTest_.*log", "", "RecordingGame1_.*dmp", "RecordingGame2_.*dmp")
        } else {
            arrayOf("ReactionTest_.*log", "", "RecordingGame2_.*dmp", "RecordingGame1_.*dmp")
        }
    )
    gui.show()
}

fun runDemo(timeMinutes: Double = 0.5): Boolean {
    val gamePreparation = IntermediatoryDescriptorFrame("""
        Toto je automaticky hrané demo.

        Hra se bude 30 sekund hrát "sama". Slouží pouze pro ilustraci, co Vás čeká.

        Stiskněte tlačítko pokračovat.
        """.trimIndent())
    if (!gamePreparation.waitUntilInteraction())
        return false

    val gameDescription = CrouchGameDescription()
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer("Demo (30 sekund)")

    val levelGenerator = DeterministicSeeds(SimpleLevelGenerator(), 2018031)
    val playerController = DFSPlayerController(dfs = DelayedTwinDFS(0.15))

    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )

    game.run((timeMinutes * 60 * 1000).toLong())

    return !game.endedFromVisualizer
}

fun runGame1(timeMinutes: Double = 5.0): Boolean {
    val gamePreparation = IntermediatoryDescriptorFrame("""
        Právě spouštíte první hru. Ve hře ovládáte modrou postavu (obdélník),
        která se sama pohybuje směrem vpravo. Při stisknutí klávesy "šipka nahoru" postava vyskočí,
        a při stisknutí klávesy "šipka dolů" se skrčí. Vašim cílem je nenarazit do překážek a dostat se co nejdále.

        Při naražení do překážky se hra restartuje a budete hrát opět od počátku - ale jinou úroveň.
        Hra se sama ukončí po uplynutí pěti minut, prosím, neukončujte hru do té doby žádným způsobem.

        Pokud je Vám vše jasné, klikněte na tlačítko "Pokračovat". Hra se ihned spustí.
        """.trimIndent())
    if (!gamePreparation.waitUntilInteraction())
        return false

    val gameNumber = if (startsWithGame1) 1 else 2
    val gameDescription = CrouchGameDescription()
    val visualiser = GamePanelVisualizer("Hra 1 (5 minut)")

    val levelGenerator = getLevelGeneratorForGame(gameNumber)
    val playerController = RecordingWrapper(KeyboardPlayerController())

    val timingAnnouncer = createTimingAnnouncer()
    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription,
            updateCallback = { game -> timingAnnouncer.onUpdate(game); },
            freezeOnStartSeconds = 1.0
    )


    val originalGameOver = game.onGameOver
    game.onGameOver = { println("Visualizer"); visualiser.announce("Hra byla restartována"); originalGameOver(); }
    game.run((timeMinutes * 60 * 1000).toLong())

    if (!game.endedFromVisualizer) {
        playerController.saveToFile("RecordingGame${gameNumber}_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
        return true
    }
    return false
}

fun runGame2(timeMinutes: Double = 5.0): Boolean {
    val gamePreparation = IntermediatoryDescriptorFrame("""
        Právě spouštíte druhou hru, která má stejný vzhled i ovládání jako hra první.

        Rozdílem jsou jinak vytvořené úrovňe.

        Opět, při stisknutí klávesy "šipka nahoru" postava vyskočí, a při stisknutí klávesy "šipka dolů" se skrčí.
        Vašim cílem je nenarazit do překážek a dostat se co nejdále.

        Při naražení do překážky se hra restartuje a budete hrát opět od počátku - ale jinou úroveň.
        Hra se sama ukončí po uplynutí pěti minut, prosím, neukončujte hru do té doby žádným způsobem.

        Pokud je Vám vše jasné, klikněte na tlačítko "Pokračovat". Hra 2 se ihned spustí.
        """.trimIndent())
    if (!gamePreparation.waitUntilInteraction())
        return false

    val gameNumber = if (startsWithGame1) 2 else 1

    val gameDescription = CrouchGameDescription()
    val visualiser = GamePanelVisualizer("Hra 2 (5 minut)")

    val levelGenerator = getLevelGeneratorForGame(gameNumber)
    val playerController = RecordingWrapper(KeyboardPlayerController())

//    val playerController = RecordingWrapper(DFSPlayerController(DelayedTwinDFS(0.25)))

    val timingAnnouncer = createTimingAnnouncer()
    val game = Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription,
            updateCallback = { game -> timingAnnouncer.onUpdate(game); },
            freezeOnStartSeconds = 1.0
    )

    val originalGameOver = game.onGameOver
    game.onGameOver = { println("Visualizer"); visualiser.announce("Hra byla restartována"); originalGameOver(); }
    game.run((timeMinutes * 60 * 1000).toLong())

    if (!game.endedFromVisualizer) {
        playerController.saveToFile("RecordingGame${gameNumber}_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp")
        return true
    }
    return false
}

fun createTimingAnnouncer(): TimingAnnouncer {
    return TimingAnnouncer(hashMapOf(
        Pair(60.0, "Zbývají 4 minuty"),
        Pair(120.0, "Zbývají 3 minuty"),
        Pair(180.0, "Zbývají 2 minuty"),
        Pair(240.0, "Zbývá 1 minuta"),
        Pair(270.0, "Zbývá 30 sekund"),
        Pair(295.0, "Hra se ukončí za 5 sekund")
    ))
}

fun getLevelGeneratorForGame(order: Int): LevelGenerator {
    return if (order == 1) {
        DFSEnsuring(DeterministicSeeds(SimpleLevelGenerator(), 2018031101), BasicDFS())
    } else {
        DelayedTwinDFSLevelGenerator(0.25, DeterministicSeeds(SimpleLevelGenerator(), 2018031102))
    }
}

fun runGame() {
}