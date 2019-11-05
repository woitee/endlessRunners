package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.Grid2D
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.DashingGameDescription
import cz.woitee.endlessRunners.game.descriptions.WanabaltGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController
import cz.woitee.endlessRunners.utils.StopWatch

/**
 * Created by woitee on 09/01/2017.
 */

fun testBasic() {
    val stopWatch = StopWatch()
    stopWatch.start()
    println("Hello world!")
    var res = stopWatch.stop()
    println("Time to print hello world, ${res}ms")
    stopWatch.start()
    println("Hello world!")
    res = stopWatch.stop()
    println("Time to print hello world, ${res}ms")

    val grid = Grid2D<GameObject?>(3, 2, { null })
    grid[0, 0] = SolidBlock()
    grid[0, 1] = SolidBlock()
    grid[1, 0] = SolidBlock()
    grid[2, 0] = SolidBlock()
    grid[2, 1] = SolidBlock()
    grid.debugPrint()

    println("After")
    grid.shiftY(5)
    grid.debugPrint()
}

fun test() {
    //Moving by x:1.40, y:-0.54 from (514.10, 24.52)
//    val game = cz.woitee.endlessRunners.createGame()
//    val locs = game.currentState.gridLocationsBetween(514.10, 24.52, 514.10 + 1.40, 24.52 - 0.54)
//    println(locs)
}

fun createGame(): Game {
//    val gameDescription = BitTripGameDescription()
//    val gameDescription = CrouchGameDescription()
//    val gameDescription = WanabaltGameDescription()
    val gameDescription = DashingGameDescription()

    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()
//    val visualiser: GamePanelVisualizer? = null

//    val levelGenerator = StateRemembering(DFSEnsuring(SimpleLevelGenerator(), BasicDFS(), doDFSAfterFail = true))
//    val levelGenerator = DFSEnsuring(SimpleLevelGenerator(), BasicDFS())
//    val levelGenerator = DelayedTwinDFSLevelGenerator(0.25, HeightBlockLevelGenerator(gameDescription))
//    val levelGenerator = FlatLevelGenerator()
//    val levelGenerator = HeightBlockLevelGenerator(gameDescription)
    val levelGenerator = SimpleLevelGenerator()

    val playerController = DFSPlayerController(BasicDFS())
//    val playerController = KeyboardPlayerController()
//    val playerController = RandomPlayerController()

    return Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )
}

fun runGame() {
    val game = createGame()
    game.gameState.tag = "Main"
    game.run()
}

fun main(args: Array<String>) {
    runGame()
//    cz.woitee.endlessRunners.test()
}