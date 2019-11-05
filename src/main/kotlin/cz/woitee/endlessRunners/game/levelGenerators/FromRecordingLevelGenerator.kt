package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject
import java.io.File
import java.util.ArrayList

class FromRecordingLevelGenerator(recordingFilePath: String) : LevelGenerator() {
    val recordingLines = File(recordingFilePath).readLines().filter { it.startsWith("NEWCOLUMN") }
    var currentIndex = 0
    val currentLine
        get() = recordingLines[currentIndex]

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val column = generateEmptyColumn()

        if (currentIndex < recordingLines.count()) {
            val justObjectChars = currentLine.substring("NEWCOLUMN(".length .. currentLine.length - 2)
            justObjectChars.forEachIndexed { i, char -> column[i] = gameState.game.gameDescription.charToObject[char]?.makeCopy() }
        }
        ++currentIndex
        return column
    }

    override fun init(gameState: GameState) {
    }
}
