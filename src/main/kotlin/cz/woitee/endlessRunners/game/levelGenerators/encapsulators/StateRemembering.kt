package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.WidthBlocks
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import java.io.File
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * A LevelGenerator encapsulator that saves copies of states from which it was called, useful for debugging.
 * Set number of states to remember, defaults to one screen.
 */
class StateRemembering(val innerGenerator: LevelGenerator, val rememberCount: Int = WidthBlocks): LevelGenerator() {
    val states = ArrayDeque<GameState>()

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (states.count() > rememberCount) {
            states.pollFirst()
        }
        states.addLast(gameState.makeCopy())
        return innerGenerator.generateNextColumn(gameState)
    }

    override fun init(gameState: GameState) {
        innerGenerator.init(gameState)
    }

    fun dumpAll() {
        val folderPath = "out/states/GameStates_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val it = states.iterator()
        var i = 0
        while (it.hasNext()) {
            val file = File(folderPath + "/$i.dmp")
            val oos = ObjectOutputStream(file.outputStream())
            val gameState: GameState = it.next()
            gameState.writeObject(oos)
            oos.flush()
            oos.close()
            ++i
        }
    }
}