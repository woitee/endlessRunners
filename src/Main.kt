import GUI.GamePanelVisualizer
import Game.Game
import Game.GameObjects.GameObject
import Game.Grid2D
import Game.PCG.*
import Game.PlayerControllers.*
import Utils.StopWatch

/**
 * Created by woitee on 09/01/2017.
 */

fun test() {
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
    grid[0, 0] = GameObject()
    grid[0, 1] = GameObject()
    grid[1, 0] = GameObject()
    grid[2, 0] = GameObject()
    grid[2, 1] = GameObject()
    grid.debugPrint()

    println("After")
    grid.shiftY(5)
    grid.debugPrint()
}

fun main(args: Array<String>) {
    val visualiser = GamePanelVisualizer()
    val levelGenerator = FlatLevelGenerator()
    val playerController = RandomPlayerController()

    val game = Game(levelGenerator, playerController, visualiser)
    game.run()
}