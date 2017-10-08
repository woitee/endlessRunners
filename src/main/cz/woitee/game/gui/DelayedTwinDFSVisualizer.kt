package cz.woitee.game.gui

import cz.woitee.game.algorithms.DelayedTwinDFS
import cz.woitee.utils.TimedThread

class DelayedTwinDFSVisualizer(val delayedTwinDFS: DelayedTwinDFS) {
    val currentStateVisualizer = GamePanelVisualizer("TwinDFS: Current State")
    val delayedStateVisualizer = GamePanelVisualizer("TwinDFS: Delayed State")

    val currentStateThread: TimedThread = TimedThread({
        if (delayedTwinDFS.currentState != null) {
            currentStateVisualizer.update(delayedTwinDFS.currentState!!)
        }},
        75.0
    )
    val delayedStateThread: TimedThread = TimedThread({
        if (delayedTwinDFS.delayedState != null) {
            delayedStateVisualizer.update(delayedTwinDFS.delayedState!!)
        }},
        75.0
    )

    fun start() {
        currentStateVisualizer.frame.setLocation(700, 0)
        delayedStateVisualizer.frame.setLocation(700, 450)

        currentStateThread.start()
        delayedStateThread.start()
    }

    fun stop(awaitJoin: Boolean = true) {
        currentStateThread.stop()
        delayedStateThread.stop()
        if (awaitJoin) {
            currentStateThread.join()
            delayedStateThread.join()
        }
    }
}