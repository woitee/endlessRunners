package cz.woitee.endlessRunners.game.gui

import java.awt.Color
import java.awt.Graphics

class DebugTextComponent {
    var text = ""
    /**
     * Draws all messages on screen.
     */
    fun draw(g: Graphics) {
        g.color = Color.RED
        g.drawString(text, 5, 10)
    }
}
