package cz.woitee.game.algorithms

import cz.woitee.game.*
import cz.woitee.game.actions.ChangeShapeAction
import cz.woitee.game.actions.JumpAction
import cz.woitee.game.actions.abstract.GameButtonAction
import cz.woitee.game.algorithms.dfs.CachedState
import cz.woitee.game.algorithms.dfs.delayedTwin.ButtonModel
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.descriptions.GameDescription
import cz.woitee.game.gui.DelayedTwinDFSVisualizer
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.game.playerControllers.NoActionPlayerController
import cz.woitee.utils.arrayList
import cz.woitee.utils.readFromFile
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.ObjectInputStream
import java.util.*

internal class DelayedTwinDFSTest {
    class TimedChangeShapeGameDescription (time: Double): BitTripGameDescription() {
        override val allActions: List<GameButtonAction> = listOf(
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

        override fun init(gameState: GameState) {
        }
    }
    data class TestPreparation(val gameState: GameState, val levelGenDFS: DelayedTwinDFS, val playerDFS: DelayedTwinDFS) {
        val levelGenerator: DFSEnsuring
            get() = gameState.levelGenerator as DFSEnsuring
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

    private fun correctCaching(delayTime: Double = 0.25, minCrouchTime: Double = 0.25) {
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
        runTestFromFile("test/data/GameStates_2017_10_02-00_45_4417.dmp", 0.2, 1)
    }

    private fun runTestFromFile(
            filePath: String,
            twinDFSdelay: Double,
            serializationVersion: Int,
            gameDescription: GameDescription = TimedChangeShapeGameDescription(0.25),
            runTime: Double = 5.0,
            expectGameOver: Boolean = false,
            allowSearchInBeginning: Boolean = false) {
        val dfsTest = BasicDFSTest()

        val delayedTwinDFS = DelayedTwinDFS(twinDFSdelay, allowSearchInBeginning = allowSearchInBeginning)
        val delayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
        delayedTwinDFSVisualizer.start()
        dfsTest.runTestFromFile(
                filePath,
                serializationVersion,
                dfsProvider = delayedTwinDFS,
                time = runTime,
                gameDescription = gameDescription,
                expectGameOver = expectGameOver
        )
        delayedTwinDFSVisualizer.dispose()
    }

    @org.junit.jupiter.api.Test
    fun searchBeginningsAndEnds_stillFoundAWay() {
        runTestFromFile("out/states/GameStates_2017_10_29-18_17_44/28.dmp", 0.25, 2, BitTripGameDescription())
    }

    @org.junit.jupiter.api.Test
    fun searchBeginningsAndEnds_stillFoundAWay2() {
        runTestFromFile("out/states/GameStates_2017_10_29-18_17_44/29.dmp", 0.25, 2, BitTripGameDescription())
    }

    @org.junit.jupiter.api.Test
    fun testSearchInLastGeneratedStates() {
        val folderPath = "out/states/GameStates_2017_11_26-22_02_24"
        for (i in 31 .. 31) {
            println("Doing $i")
            runTestFromFile("$folderPath/$i.dmp", 0.25, 4, BitTripGameDescription(), allowSearchInBeginning = true)
        }
    }

    @org.junit.jupiter.api.Test
    fun bugShrinkingGapAfterSearch() {
        val currentStatePath = "out/states/WrongCurrent_2017_12_03-18_32_26_255.dmp"
        val delayedStatePath = "out/states/WrongDelayed_2017_12_03-18_32_26_261.dmp"

        val game = Game(SimpleLevelGenerator(), NoActionPlayerController(), GamePanelVisualizer(),
                mode = Game.Mode.INTERACTIVE,
                gameDescription = BitTripGameDescription()
        )

        val currentState = game.gameState.makeCopy()
        currentState.readFromFile(currentStatePath)

        val delayedState = game.gameState.makeCopy()
        delayedState.readFromFile(delayedStatePath)

        val buttonModel = ButtonModel(
            currentState, delayedState, game.updateTime
        )
        val delayedTwinDFS = DelayedTwinDFS(0.25)
        delayedTwinDFS.init(game.gameState)
        delayedTwinDFS.buttonModel = buttonModel
        delayedTwinDFS.searchForAction(game.gameState)
    }

    fun prepareData(filePath: String, serializationVersion: Int = 4): TestPreparation {
        val delayedTwinDFS = DelayedTwinDFS(0.25, allowSearchInBeginning = true)
        val delayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
        delayedTwinDFSVisualizer.start()
        val levelGenerator = DFSEnsuring(SimpleLevelGenerator(), delayedTwinDFS, doDFSAfterFail = true, dumpErrors = false)
        val playerDFS = DelayedTwinDFS(0.25)
        val game = Game(
                levelGenerator,
                DFSPlayerController(playerDFS),
//                null,
                GamePanelVisualizer(),
                mode = Game.Mode.INTERACTIVE,
                gameDescription = BitTripGameDescription(),
                restartOnGameOver = false
        )
        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        game.gameState.serializationVersion = serializationVersion
        game.gameState.readObject(ois)

        return TestPreparation(game.gameState, delayedTwinDFS, playerDFS)
    }

    fun tryFailOFCopyColumn(testPreparation: TestPreparation) {
        val game = testPreparation.gameState.game

        game.levelGenerator.generateNextColumn(testPreparation.gameState)

        assertNotEquals(DFSEnsuring.DFSResult.FAIL_COPYCOLUMN, testPreparation.levelGenerator.lastResult)
    }

    fun validateStackInDFS(testPreparation: TestPreparation) {
        val gameState = testPreparation.gameState
        val delayedTwinDFS = testPreparation.levelGenDFS

        val stateCopy = gameState.makeCopy()
        delayedTwinDFS.searchForAction(gameState)
        for (stackData in delayedTwinDFS.dfsStack.reversed()) {
            assertEquals(CachedState(stateCopy), stackData.cachedState)
            var stateChange: GameButton.StateChange? = null
            if (stackData.action != null) {
                val buttonIx = stackData.action?.button ?: 0
                val interactionType = if (stackData.action!!.isPress) GameButton.InteractionType.HOLD else GameButton.InteractionType.RELEASE

                stateChange = stateCopy.buttons[buttonIx].interact(interactionType)
            }
            stateCopy.advanceUndoableByAction(stateChange, gameState.game.updateTime)
        }
    }

    @org.junit.jupiter.api.Test
    fun statesNotDisabledAtEndOfSearch() {
        val dummyGameState = DummyObjects.createDummyGameState()
        val delayedTwinDFS = DelayedTwinDFS(0.25)
        delayedTwinDFS.init(dummyGameState)

        delayedTwinDFS.searchForAction(dummyGameState)

        assertTrue(delayedTwinDFS.lastStats.success, "The simple search should succeed")
        assertFalse(delayedTwinDFS.buttonModel!!.delayedStateDisabled, "Delayed State should not be disabled at end of successful search")
        assertFalse(delayedTwinDFS.buttonModel!!.currentStateDisabled, "Current State should not be disabled at end of successful search")
    }

    @org.junit.jupiter.api.Test
    fun consistentSearchExample1() {
        consistentSearchInSubsequentSteps("out/buttonModels/ButtonModel_2018_01_21-15_37_41.dmp")
    }

    @org.junit.jupiter.api.Test
    fun consistentSearchExample2() {
        consistentSearchInSubsequentSteps("out/buttonModels/ButtonModel_2018_01_21-16_36_41.dmp")
    }

    @org.junit.jupiter.api.Test
    fun consistentSearchInSubsequentSteps(filePath: String) {
        val firstRes = performDFSFromButtonModel(filePath, 2)
        val secondRes = performDFSFromButtonModel(filePath, 1)
        assertEquals(firstRes, secondRes, "The second run should end exactly like first run!")
        println("Both runs are ${if (firstRes) "succeeding" else "failing!"}")
    }

    fun performDFSFromButtonModel(filePath: String, readTimes: Int = 1): Boolean {
        val dummyGame = Game(FlatLevelGenerator(), NoActionPlayerController(), null, gameDescription = BitTripGameDescription())
        val dummyState = dummyGame.gameState

        val delayedTwinDFS = DelayedTwinDFS(0.25)
        delayedTwinDFS.init(dummyState)

        val file = File(filePath)
        val ois = ObjectInputStream(file.inputStream())
        for (i in 1 .. readTimes) {
            delayedTwinDFS.buttonModel!!.readObject(ois)
        }

        val gameState = delayedTwinDFS.buttonModel!!.delayedState

        val visualizer: DelayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
        visualizer.start()

        delayedTwinDFS.searchForAction(gameState)
        println("DelayedTwinDFS stats: ${delayedTwinDFS.lastStats}")
        visualizer.stop()

        return delayedTwinDFS.lastStats.success
    }
}