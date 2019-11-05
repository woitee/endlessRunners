package cz.woitee.endlessRunners.utils

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.gui.DelayedTwinDFSVisualizer
import java.text.SimpleDateFormat
import java.util.*

/**
 * Debugging utils, mainly for Delayed Twin DFS.
 */
object DebugUtils {
    var delayedTwinDFSVisualizer: DelayedTwinDFSVisualizer? = null

    fun attachDelayedTwinVisualizer(delayedTwinDFS: DelayedTwinDFS) {
        // Recommended breakpoint in DelayedTwinDFS::searchInternal

        val delayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
        delayedTwinDFS.sleepTime = 50
        delayedTwinDFSVisualizer.init()
    }

    fun deattachDelayedTwinVisualizer() {
        delayedTwinDFSVisualizer?.dispose()
    }

    fun printDebugInfo(gameState: GameState) {
        println("PlayerX:${gameState.player.x} PlayerY:${gameState.player.y} PlayerXSpeed:${gameState.player.xspeed} PlayerYSpeed:${gameState.player.yspeed}")
        println("HeldActionsAsFlags:${gameState.heldButtonsAsFlags()}")
        gameState.print()
    }

    val currentDateTimeString: String
        get() = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
}
