import GUI.GamePanelVisualizer
import Game.Game
import Game.GameState
import Game.GameObjects.GameObject
import Game.GameObjects.SolidBlock
import Game.Grid2D
import Game.PCG.*
import Game.PlayerControllers.*
import Utils.StopWatch

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
    val game = createGame()
    val coll = game.collHandler.nearestCollision(game.gameState, 5.0, 15.0, 25.0, 5.0)
    println(coll)
}

fun createGame(): Game {
    val visualiser = GamePanelVisualizer()
    val levelGenerator = FlatLevelGenerator()
    val playerController = RandomPlayerController()

    return Game(levelGenerator, playerController, visualiser)
}

fun runGame() {
    createGame().run()
}

fun main(args: Array<String>) {
    runGame()
//    test()
}