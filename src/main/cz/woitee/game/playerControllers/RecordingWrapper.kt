package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState
import cz.woitee.game.WidthBlocks
import java.io.File

class RecordingWrapper(val innerController: PlayerController): PlayerController() {
    val recording = ArrayList<String>()

    var lastGridX = 0

    override fun init(gameState: GameState) {
        innerController.init(gameState)
    }
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        if (gameState.gridX > lastGridX) {
            lastGridX = gameState.gridX
            val charColumn = gameState.grid.getColumn(WidthBlocks - 1).map { it?.dumpChar ?: '#' }.joinToString()

            recording.add("NEWCOLUMN($charColumn)")
        }

        val stateChange = innerController.onUpdate(gameState)
        recording.add(stateChange.toString())

        return stateChange
    }

    fun saveToFile(filename: String) {
        println("Saving recording to file $filename")
        val file = File(filename)
        val writer = file.bufferedWriter()
        for (line in recording) {
            writer.write(line)
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }
}