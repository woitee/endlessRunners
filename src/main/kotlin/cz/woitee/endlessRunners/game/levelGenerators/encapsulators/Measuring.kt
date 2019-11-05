package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import java.text.DecimalFormat
import java.util.*

class Measuring(val innerGenerator: LevelGenerator, val sumEvery: Int = 10): LevelGenerator() {
    var timeSum = 0.0
    var runCount = 0
    val df = DecimalFormat("#.##")

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val startTime = System.nanoTime()

        val column = innerGenerator.generateNextColumn(gameState)

        val time = (System.nanoTime() - startTime).toDouble() / 1000000.0
        runCount += 1
        timeSum += time
        if (runCount >= sumEvery) {
            println("Measure of $runCount runs of generator - avg. time is ${df.format(timeSum/runCount)}ms")
            runCount = 0
            timeSum = 0.0
        }

        return column
    }

    override fun init(gameState: GameState) {
        innerGenerator.init(gameState)
    }
}