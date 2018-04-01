package cz.woitee.utils

import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.gui.DelayedTwinDFSVisualizer

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
}