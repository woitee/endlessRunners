package cz.woitee.endlessRunners.game.gui

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.utils.TimedThread
import java.awt.Graphics

class DelayedTwinDFSVisualizer(val delayedTwinDFS: DelayedTwinDFS, val frameX: Int = 700, val frameY: Int = 0): GamePanelVisualizer() {
    val visualizeThread: TimedThread = TimedThread({
            this.update(delayedTwinDFS.buttonModel.currentState)
        },
        75.0
    )

    val currentState
        get() = delayedTwinDFS.buttonModel.currentState
    val delayedState
        get() = delayedTwinDFS.buttonModel.delayedState

    fun start() {
        frame.setLocation(frameX, frameY)
        frame.title = "Visualization"
        visualizeThread.start()
    }

    override fun drawEverything(gameState: GameState, g: Graphics) {
        super.drawEverything(gameState, g)
        drawGameObject(delayedState.player, g, currentState.player.x)
    }

    fun stop() {
        visualizeThread.stop(true)
        dispose()
    }
}