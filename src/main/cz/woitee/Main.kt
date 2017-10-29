package cz.woitee

import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.Game
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.Grid2D
import cz.woitee.game.algorithms.DFS
import cz.woitee.game.algorithms.DelayedTwinDFS
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.Measuring
import cz.woitee.game.levelGenerators.encapsulators.StateRemembering
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.utils.StopWatch
import java.util.concurrent.Delayed

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
//    val game = cz.woitee.createGame()
//    val locs = game.gameState.gridLocationsBetween(514.10, 24.52, 514.10 + 1.40, 24.52 - 0.54)
//    println(locs)
}

fun createGame(): Game {
//    val gameDescription = BitTripGameDescription()
    val gameDescription = BitTripGameDescription()

    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()
//    val visualiser: GamePanelVisualizer? = null

//    val levelGenerator = TestLevelGenerator()
    val levelGenerator = StateRemembering(DFSEnsuring(SimpleLevelGenerator(), DelayedTwinDFS(0.25)))
//    val levelGenerator = DFSEnsuring(BlockLevelGenerator(gameDescription))

    val playerController = DFSPlayerController(DelayedTwinDFS(0.25))
//    val playerController = KeyboardPlayerController()
//    val playerController = RandomPlayerController()

    return Game(levelGenerator, playerController, visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )
}

fun runGame() {
    createGame().run()
}

fun main(args: Array<String>) {
    runGame()
//    cz.woitee.test()
}