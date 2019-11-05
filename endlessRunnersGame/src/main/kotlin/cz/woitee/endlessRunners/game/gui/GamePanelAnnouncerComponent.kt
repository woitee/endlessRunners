package cz.woitee.endlessRunners.game.gui

import cz.woitee.endlessRunners.game.GameHeight
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.PlayerScreenX
import java.awt.Color
import java.awt.Graphics
import java.util.*
import kotlin.math.roundToInt

/**
 * A component of a GamePanel that draws floating messages above the Player, each for a predetermined time.
 */
class GamePanelAnnouncerComponent {
    data class QueuedMessage(val startTime: Long, val message: String)
    /** Time of announcement in seconds. */
    var announceTime = 1.5
    val announceTimeMillis
        get() = (announceTime * 1000).roundToInt()

    protected val announceX = 10
    protected val announceYStart = 5
    protected val announceYEnd = 30

    protected val queue = ArrayDeque<QueuedMessage>()

    /**
     * Adds a message to the currently announced.
     */
    fun startAnnouncing(message: String) {
        queue.add(QueuedMessage(System.currentTimeMillis(), message))
    }

    /**
     * Draws all messages on screen.
     */
    fun draw(g: Graphics, gameState: GameState) {
        val playerY = GameHeight - gameState.player.y.toInt() - gameState.player.heightPx
        val currentTime = System.currentTimeMillis()

        // Dispose unneeded
        while (!queue.isEmpty() && queue.peek().startTime + (announceTimeMillis) < currentTime) {
            queue.poll()
        }

        g.color = Color.BLACK
        for (queuedM in queue) {
            val proportion = (currentTime - queuedM.startTime).toDouble() / announceTimeMillis
            val messageY = announceYStart + (proportion * announceYEnd - announceYStart)

            g.drawString(
                queuedM.message,
                PlayerScreenX.toInt(),
                playerY - messageY.roundToInt()
            )
        }
    }
}
