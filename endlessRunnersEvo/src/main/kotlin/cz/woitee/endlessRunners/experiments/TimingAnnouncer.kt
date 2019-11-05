package cz.woitee.endlessRunners.experiments

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer

/**
 * Creates announcements in given time-marks (time is given in seconds).
 */
class TimingAnnouncer(val time2Message: HashMap<Double, String>) {
    var firstUpdate = -1L
    var lastUpdate = -1L

    fun onUpdate(game: Game) {
        val gameVisualizer = game.visualizer as? GamePanelVisualizer ?: return

        val thisUpdate = System.currentTimeMillis()
        if (firstUpdate == -1L) firstUpdate = thisUpdate
        if (lastUpdate == -1L) lastUpdate = thisUpdate

        val timeSinceFirst = (thisUpdate - firstUpdate).toDouble() / 1000
        val lastTimeSinceFirst = (lastUpdate - firstUpdate).toDouble() / 1000

        for ((time, message) in time2Message) {
            if (time >= lastTimeSinceFirst && time < timeSinceFirst) {
                gameVisualizer.announce(message)
            }
        }

        lastUpdate = thisUpdate
    }
}
