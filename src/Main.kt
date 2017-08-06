import gui.GamePanelVisualizer
import game.Game
import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.Grid2D
import game.pcg.*
import game.playerControllers.*
import utils.StopWatch
import game.gameDescriptions.*

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
//    val game = createGame()
//    val locs = game.gameState.gridLocationsBetween(514.10, 24.52, 514.10 + 1.40, 24.52 - 0.54)
//    println(locs)



}

fun createGame(): Game {
    val visualiser: GamePanelVisualizer? = GamePanelVisualizer()
//    val visualiser: GamePanelVisualizer? = null

    val levelGenerator = TestLevelGenerator()
//    val levelGenerator = DFSEnsuringGenerator(SimpleLevelGenerator())

    val playerController = DFSPlayerController()
//    val playerController = KeyboardPlayerController()
//    val playerController = RandomPlayerController()

    val gameDescription = BitTripGameDescription()
//    val gameDescription = GameDescription()

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
//    test()
}