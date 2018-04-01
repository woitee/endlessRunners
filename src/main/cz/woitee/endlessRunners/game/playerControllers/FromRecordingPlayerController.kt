package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState
import java.io.File

class FromRecordingPlayerController(recordingFilePath: String): PlayerController() {

    val recordingLines = File(recordingFilePath).readLines().filter { !it.startsWith("NEWCOLUMN") }
    var currentIndex = 0
    val currentLine
        get() = recordingLines[currentIndex]

    override fun init(gameState: GameState) {
        if (currentIndex >= recordingLines.count()) return
        if (currentLine != "INIT") throw Exception("Init called when it was not expected, instead $currentLine")
        ++currentIndex
    }

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        if (currentIndex >= recordingLines.count()) return null
        val stateChange = if (currentLine == "null") {
            null
        } else {
            if (!currentLine.startsWith("StateChange")) throw Exception("StateChange when no state, instead $currentLine")
            GameButton.StateChange.fromString(gameState, currentLine)
        }
        ++currentIndex
        return stateChange
    }
}