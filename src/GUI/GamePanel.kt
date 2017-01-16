package GUI

import javax.swing.JPanel
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image

/**
 * Class to learn from - Killer Game Programming in Java.
 * Created by woitee on 02/01/2017.
 */

class GamePanel: JPanel, Runnable {
    var running = false
    var gameOver = false

    // for rendering
    var dbg: Graphics? = null
    var dbImage: Image? = null

    var animator = Thread(this)

    constructor(width: Int, height: Int) : super() {
        background = Color.white
        preferredSize = Dimension(width, height)
        dbImage = createImage(width, height)

        isFocusable = true
        requestFocus()
    }

    public override fun addNotify() {
        super.addNotify()
        startGame()
    }

    private fun startGame() {
        if (!running) {
            animator.start()
        }
    }

    private fun stopGame() {
        running = false
    }

    private fun gameUpdate() {}

    public override fun run() {
        running = true
        while (running) {
            gameUpdate()
            gameRender()
            repaint()

            try {
                Thread.sleep(20)
            } catch (e: InterruptedException) {
            }
        }
        System.exit(0)
    }

    private fun gameRender() {
    }
}
