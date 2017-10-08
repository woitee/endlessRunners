package cz.woitee.game.algorithms

import cz.woitee.game.Game
import cz.woitee.game.GameState
import cz.woitee.game.HeightBlocks
import cz.woitee.game.actions.ChangeShapeAction
import cz.woitee.game.actions.JumpAction
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.gui.DelayedTwinDFSVisualizer
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.utils.arrayList
import game.algorithms.DFSTest
import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class DelayedTwinDFSTest {
    class TimedChangeShapeGameDescription (time: Double): BitTripGameDescription() {
        override val allActions: List<GameAction> = listOf(
                JumpAction(22.0),
                ChangeShapeAction(2, 1, time)
        )
    }
    class HolesLevelGenerator(val holeWidth: Int = 2): LevelGenerator() {
        override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
            val col = arrayList<GameObject?>(HeightBlocks, { null })
            col[0] = SolidBlock()
            val gridX = gameState.gridX

            if (gridX % 20 in 1 .. holeWidth) {
            } else {
                col[1] = SolidBlock()
            }

            return col
        }

        override fun reset() {
        }
    }

    @org.junit.jupiter.api.Test
    fun allCorrectCaching() {
//        val possibleValues = arrayOf(0.1, 0.25, 0.3, 0.5)
        val possibleValues = arrayOf(0.25)
        for (delayTime in possibleValues) {
            for (minCrouchTime in possibleValues) {
                println("Trying delayTime:$delayTime minCrouchTime:$minCrouchTime")
                correctCaching(delayTime, minCrouchTime)
            }
        }
    }

    fun correctCaching(delayTime: Double = 0.25, minCrouchTime: Double = 0.25) {
        // We mainly need the min time-limit on ChangeShapeAction
        val gameDescription = TimedChangeShapeGameDescription(minCrouchTime)
        val levelGenerator = HolesLevelGenerator()

        val delayedTwinDFS = DelayedTwinDFS(delayTime)
        val playerController = DFSPlayerController(delayedTwinDFS)
        val visualizer = GamePanelVisualizer()
        val game = Game(levelGenerator, playerController, visualizer,
                mode = Game.Mode.INTERACTIVE,
                gameDescription = gameDescription,
                restartOnGameOver = false
        )

        var exceptionMessage = ""
            game.updateThread.thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e -> exceptionMessage = e.message ?: "No Message" }

        game.start()
        game.updateThread.join(7000)
        game.stop()
        assertEquals(false, game.gameState.isGameOver)
        assertEquals("", exceptionMessage, "Exception! DelayTime: $delayTime. MinCrouchTime: $minCrouchTime. ExceptionMessage: $exceptionMessage")
    }

    @org.junit.jupiter.api.Test
    fun multipleActionsOnStackForDelayedState() {
        val dfsTest = DFSTest()

        val delayedTwinDFS = DelayedTwinDFS(0.1)
//        val delayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
//        delayedTwinDFSVisualizer.start()
        dfsTest.runTestFromFile(
                "test/data/GameStates_2017_10_02-00_45_4417.dmp",
                dfsProvider = delayedTwinDFS,
                time = 5.0,
                gameDescription = TimedChangeShapeGameDescription(0.25)
        )
    }
}