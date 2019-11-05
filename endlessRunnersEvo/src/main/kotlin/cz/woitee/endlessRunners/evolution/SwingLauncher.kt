package cz.woitee.endlessRunners.cz.woitee.endlessRunner.evolution

import cz.woitee.endlessRunners.evolution.coevolution.evolved.CoevolvedTriples
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.evolved.BestEvolvedBitTriController
import cz.woitee.endlessRunners.evolution.evoController.evolved.BestEvolvedCanabalController
import cz.woitee.endlessRunners.evolution.evoController.evolved.BestEvolvedChameleonController
import cz.woitee.endlessRunners.evolution.evoController.evolved.BestEvolvedCrouchController
import cz.woitee.endlessRunners.evolution.evoGame.evolved.BestEvolvedGameDescriptions
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.CanabalGameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.ChameleonGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.CanabalLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.game.playerControllers.RandomPlayerController
import cz.woitee.endlessRunners.gameLaunchers.bitTriGameDefaultBlocks
import cz.woitee.endlessRunners.gameLaunchers.chameleonGameDefaultBlocks
import java.awt.Color
import java.awt.Component
import javax.swing.*

/**
 * A swing launcher class capable of creating many different game combinations from a GUI.
 */
class SwingLauncher {
    enum class GameDescriptionOptions(val str: String) {
        Basic("Basic"),
        Crouch("Crouch"),
        Canabal("Canabal"),
        BitTri("BitTri"),
        Chameleon("Chameleon"),
        GameEvo1("Game evolution - run 1"),
        GameEvo2("Game evolution - run 2"),
        GameEvo3("Game evolution - run 3"),
        GameEvo4("Game evolution - run 4"),
        GameEvo5("Game evolution - run 5"),
        CoEvo1("CoevolvedGame - run 1"),
        CoEvo2("CoevolvedGame - run 2"),
        CoEvo3("CoevolvedGame - run 3"),
        CoEvo4("CoevolvedGame - run 4"),
        CoEvo5("CoevolvedGame - run 5"),
        CoEvo6("CoevolvedGame - run 6"),
        CoEvo7("CoevolvedGame - run 7"),
        CoEvo8("CoevolvedGame - run 8"),
        CoEvo9("CoevolvedGame - run 9"),
        CoEvo10("CoevolvedGame - run 10"),
        CoEvo11("CoevolvedGame - run 11"),
        CoEvo12("CoevolvedGame - run 12"),
        CoEvo13("CoevolvedGame - run 13"),
        CoEvo14("CoevolvedGame - run 14"),
        CoEvo15("CoevolvedGame - run 15"),
        CoEvo16("CoevolvedGame - run 16"),
        CoEvo17("CoevolvedGame - run 17"),
        CoEvo18("CoevolvedGame - run 18"),
        CoEvo19("CoevolvedGame - run 19"),
        CoEvo20("CoevolvedGame - run 20");

        fun get(): GameDescription {
            return when (this) {
                Basic -> GameDescription()
                Crouch -> CrouchGameDescription()
                Canabal -> CanabalGameDescription()
                BitTri -> BitTriGameDescription()
                Chameleon -> ChameleonGameDescription()
                GameEvo1 -> BestEvolvedGameDescriptions.getGameDescription(0)
                GameEvo2 -> BestEvolvedGameDescriptions.getGameDescription(1)
                GameEvo3 -> BestEvolvedGameDescriptions.getGameDescription(2)
                GameEvo4 -> BestEvolvedGameDescriptions.getGameDescription(3)
                GameEvo5 -> BestEvolvedGameDescriptions.getGameDescription(4)
                CoEvo1 -> CoevolvedTriples.get(0).description
                CoEvo2 -> CoevolvedTriples.get(1).description
                CoEvo3 -> CoevolvedTriples.get(2).description
                CoEvo4 -> CoevolvedTriples.get(3).description
                CoEvo5 -> CoevolvedTriples.get(4).description
                CoEvo6 -> CoevolvedTriples.get(5).description
                CoEvo7 -> CoevolvedTriples.get(6).description
                CoEvo8 -> CoevolvedTriples.get(7).description
                CoEvo9 -> CoevolvedTriples.get(8).description
                CoEvo10 -> CoevolvedTriples.get(9).description
                CoEvo11 -> CoevolvedTriples.get(10).description
                CoEvo12 -> CoevolvedTriples.get(11).description
                CoEvo13 -> CoevolvedTriples.get(12).description
                CoEvo14 -> CoevolvedTriples.get(13).description
                CoEvo15 -> CoevolvedTriples.get(14).description
                CoEvo16 -> CoevolvedTriples.get(15).description
                CoEvo17 -> CoevolvedTriples.get(16).description
                CoEvo18 -> CoevolvedTriples.get(17).description
                CoEvo19 -> CoevolvedTriples.get(18).description
                CoEvo20 -> CoevolvedTriples.get(19).description
            }
        }

        override fun toString(): String {
            return str
        }
    }

    enum class PlayerControllerOptions(protected val stringVal: String) {
        Keyboard("Keyboard"),
        Random("Random"),
        DFS("DFS"),
        DTwinDFS("Delayed Twin DFS (0.1 second delay)"),
        BestEvoCrouch("Best Evolved for Crouch"),
        BestEvoCanabal("Best Evolved for Canabal"),
        BestEvoBitTri("Best Evolved for BitTri"),
        BestEvoChameleon("Best Evolved for Chameleon"),
        CoEvo1("CoevolvedController - run 1"),
        CoEvo2("CoevolvedController - run 2"),
        CoEvo3("CoevolvedController - run 3"),
        CoEvo4("CoevolvedController - run 4"),
        CoEvo5("CoevolvedController - run 5"),
        CoEvo6("CoevolvedController - run 6"),
        CoEvo7("CoevolvedController - run 7"),
        CoEvo8("CoevolvedController - run 8"),
        CoEvo9("CoevolvedController - run 9"),
        CoEvo10("CoevolvedController - run 10"),
        CoEvo11("CoevolvedController - run 11"),
        CoEvo12("CoevolvedController - run 12"),
        CoEvo13("CoevolvedController - run 13"),
        CoEvo14("CoevolvedController - run 14"),
        CoEvo15("CoevolvedController - run 15"),
        CoEvo16("CoevolvedController - run 16"),
        CoEvo17("CoevolvedController - run 17"),
        CoEvo18("CoevolvedController - run 18"),
        CoEvo19("CoevolvedController - run 19"),
        CoEvo20("CoevolvedController - run 20");

        fun get(): PlayerController {
            return when (this) {
                Keyboard -> KeyboardPlayerController()
                Random -> RandomPlayerController()
                DFS -> DFSPlayerController()
                DTwinDFS -> DFSPlayerController(DelayedTwinDFS(0.1))
                BestEvoCrouch -> BestEvolvedCrouchController()
                BestEvoCanabal -> BestEvolvedCanabalController()
                BestEvoBitTri -> BestEvolvedBitTriController()
                BestEvoChameleon -> BestEvolvedChameleonController()
                CoEvo1 -> CoevolvedTriples.get(0).controller
                CoEvo2 -> CoevolvedTriples.get(1).controller
                CoEvo3 -> CoevolvedTriples.get(2).controller
                CoEvo4 -> CoevolvedTriples.get(3).controller
                CoEvo5 -> CoevolvedTriples.get(4).controller
                CoEvo6 -> CoevolvedTriples.get(5).controller
                CoEvo7 -> CoevolvedTriples.get(6).controller
                CoEvo8 -> CoevolvedTriples.get(7).controller
                CoEvo9 -> CoevolvedTriples.get(8).controller
                CoEvo10 -> CoevolvedTriples.get(9).controller
                CoEvo11 -> CoevolvedTriples.get(10).controller
                CoEvo12 -> CoevolvedTriples.get(11).controller
                CoEvo13 -> CoevolvedTriples.get(12).controller
                CoEvo14 -> CoevolvedTriples.get(13).controller
                CoEvo15 -> CoevolvedTriples.get(14).controller
                CoEvo16 -> CoevolvedTriples.get(15).controller
                CoEvo17 -> CoevolvedTriples.get(16).controller
                CoEvo18 -> CoevolvedTriples.get(17).controller
                CoEvo19 -> CoevolvedTriples.get(18).controller
                CoEvo20 -> CoevolvedTriples.get(19).controller
            }
        }

        override fun toString(): String {
            return stringVal
        }
    }

    enum class LevelGeneratorOptions(protected val stringVal: String) {
        Simple("Simple"),
        Flat("Flat"),
        DFSEnsuredSimple("DFS ensured Simple"),
        DelayedTwinDFSEnsuredSimple("Delayed Twin DFS ensured Simple"),
        Crouch("Default for Crouch"),
        Canabal("Default for Canabal"),
        BitTri("Default Fro BitTri"),
        Chameleon("Default for Chameleon"),
        EvolvedHeightBlocks("Evolve HeightBlocks (will take around 15 seconds)"),
        CoEvo1("CoevolvedBlocks - run 1"),
        CoEvo2("CoevolvedBlocks - run 2"),
        CoEvo3("CoevolvedBlocks - run 3"),
        CoEvo4("CoevolvedBlocks - run 4"),
        CoEvo5("CoevolvedBlocks - run 5"),
        CoEvo6("CoevolvedBlocks - run 6"),
        CoEvo7("CoevolvedBlocks - run 7"),
        CoEvo8("CoevolvedBlocks - run 8"),
        CoEvo9("CoevolvedBlocks - run 9"),
        CoEvo10("CoevolvedBlocks - run 10"),
        CoEvo11("CoevolvedBlocks - run 11"),
        CoEvo12("CoevolvedBlocks - run 12"),
        CoEvo13("CoevolvedBlocks - run 13"),
        CoEvo14("CoevolvedBlocks - run 14"),
        CoEvo15("CoevolvedBlocks - run 15"),
        CoEvo16("CoevolvedBlocks - run 16"),
        CoEvo17("CoevolvedBlocks - run 17"),
        CoEvo18("CoevolvedBlocks - run 18"),
        CoEvo19("CoevolvedBlocks - run 19"),
        CoEvo20("CoevolvedBlocks - run 20");

        fun get(gameDescription: GameDescription): LevelGenerator {
            return when (this) {
                Simple -> SimpleLevelGenerator()
                Flat -> FlatLevelGenerator()
                DFSEnsuredSimple -> DFSEnsuring(SimpleLevelGenerator(), BasicDFS())
                DelayedTwinDFSEnsuredSimple -> DFSEnsuring(SimpleLevelGenerator(), DelayedTwinDFS(0.1))
                Crouch -> SimpleLevelGenerator()
                Canabal -> CanabalLevelGenerator()
                BitTri -> HeightBlockLevelGenerator(gameDescription, bitTriGameDefaultBlocks(BitTriGameDescription()))
                Chameleon -> HeightBlockLevelGenerator(gameDescription, chameleonGameDefaultBlocks(ChameleonGameDescription()))
                EvolvedHeightBlocks -> HeightBlockLevelGenerator(gameDescription,
                        EvoBlockRunner(gameDescription, { DFSPlayerController(DelayedTwinDFS(0.1)) }).evolveMultipleBlocks(7)
                )
                CoEvo1 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(0).blocks)
                CoEvo2 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(1).blocks)
                CoEvo3 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(2).blocks)
                CoEvo4 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(3).blocks)
                CoEvo5 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(4).blocks)
                CoEvo6 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(5).blocks)
                CoEvo7 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(6).blocks)
                CoEvo8 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(7).blocks)
                CoEvo9 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(8).blocks)
                CoEvo10 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(9).blocks)
                CoEvo11 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(10).blocks)
                CoEvo12 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(11).blocks)
                CoEvo13 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(12).blocks)
                CoEvo14 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(13).blocks)
                CoEvo15 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(14).blocks)
                CoEvo16 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(15).blocks)
                CoEvo17 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(16).blocks)
                CoEvo18 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(17).blocks)
                CoEvo19 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(18).blocks)
                CoEvo20 -> HeightBlockLevelGenerator(gameDescription, CoevolvedTriples.get(19).blocks)
            }
        }

        override fun toString(): String {
            return stringVal
        }
    }

    val cbGameDescription = JComboBox(GameDescriptionOptions.values())
    val cbPlayerController = JComboBox(PlayerControllerOptions.values())
    val cbLevelGenerator = JComboBox(LevelGeneratorOptions.values())
    val startButton = JButton("Start Game")

    val frame = createFrame()

    private fun createFrame(): JFrame {
        val frame = JFrame("Experiment GUI")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pane = frame.contentPane

        val panel = createPanel()

        panel.background = Color.DARK_GRAY
        panel.isVisible = true

        panel.isFocusable = true
        panel.requestFocus()

        pane.add(panel)
        frame.pack()

        return frame
    }

    private fun createPanel(): JPanel {
        val panel = JPanel()

        val subPanel1 = JPanel()
        subPanel1.layout = BoxLayout(subPanel1, BoxLayout.Y_AXIS)
        subPanel1.add(JLabel("Game Description"))
        cbGameDescription.alignmentX = Component.LEFT_ALIGNMENT
        subPanel1.add(cbGameDescription)

        val subPanel2 = JPanel()
        subPanel2.layout = BoxLayout(subPanel2, BoxLayout.Y_AXIS)
        subPanel2.add(JLabel("Player Controller", 0))
        cbPlayerController.alignmentX = Component.LEFT_ALIGNMENT
        subPanel2.add(cbPlayerController)

        val subPanel3 = JPanel()
        subPanel3.layout = BoxLayout(subPanel3, BoxLayout.Y_AXIS)
        subPanel3.add(JLabel("Level Generator", 0))
        cbLevelGenerator.alignmentX = Component.LEFT_ALIGNMENT
        subPanel3.add(cbLevelGenerator)

        panel.add(subPanel1)
        panel.add(subPanel2)
        panel.add(subPanel3)

        panel.add(startButton)
        startButton.addActionListener { createAndRunGame() }

        return panel
    }

    protected fun createAndRunGame() {
        val gameDescription = (cbGameDescription.selectedItem as GameDescriptionOptions).get()
        val playerController = (cbPlayerController.selectedItem as PlayerControllerOptions).get()
        val levelGenerator = (cbLevelGenerator.selectedItem as LevelGeneratorOptions).get(gameDescription)
        frame.dispose()

        val game = Game(levelGenerator, playerController, GamePanelVisualizer(), gameDescription = gameDescription)
        val thread = Thread { game.run() }
        thread.start()
    }

    fun show() {
        frame.isVisible = true
    }
    fun hide() {
        frame.isVisible = false
    }
}
