package GUI

import Game.*
import Game.GameObjects.*
import java.awt.*
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * Created by woitee on 15/01/2017.
 */

class GamePanelVisualizer: IGameVisualizer {
    var frame: JFrame? = null
        private set
    var panel: JPanel = JPanel(BorderLayout())
        private set
    private var dbg: Graphics? = null
    private var dbImage: Image? = null

    private var running = false

    override fun start() {
        SwingUtilities.invokeAndWait {
            frame = createFrame()
        }
        running = true
    }

    private fun createFrame(): JFrame {
        val frame = JFrame("Endless Runners GUI")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pane = frame.contentPane

        panel.background = Color.RED
        panel.preferredSize = Dimension(GameWidth, GameHeight)
        panel.isVisible = true

        panel.isFocusable = true
        panel.requestFocus()

        pane.add(panel)

        frame.pack()
        frame.isVisible = true

        return frame
    }

    override fun stop() {
        println("Running STOP")
        SwingUtilities.invokeLater {
            frame?.isVisible = false
        }
        running = false
        // TODO dispose of everything
    }

    override fun update(gameState: GameState) {
        if (!running) {
            println("Not Running!")
            return
        }
        SwingUtilities.invokeAndWait swingThread@ {
            if (dbImage == null) {
                dbImage = panel.createImage(GameWidth, GameHeight)
                if (dbImage == null) {
                    println("DBImage is null")
                    stop()
                    return@swingThread
                }
                dbg = dbImage!!.graphics
            }

            val _dbg = dbg!!
            _dbg.color = Color.WHITE
            _dbg.fillRect(0, 0, GameWidth, GameHeight)

            val playerX = gameState.player.x

            synchronized(gameState.gameObjects) {
                for (gameObject in gameState.gameObjects)
                    drawGameObject(gameObject, _dbg, playerX)
            }

            repaint()
        }
    }

    fun drawGameObject(gameObject: GameObject, g: Graphics, playerX: Double) {
        val topLeftX = (gameObject.x - playerX + PlayerScreenX).toInt()
        val topLeftY = GameHeight - gameObject.y.toInt() - gameObject.heightPx
        drawGameObjectAt(gameObject, g, topLeftX, topLeftY)
    }

    fun drawGameObjectAt(gameObject: GameObject, g: Graphics, x: Int, y: Int) {
        val x2 = x + gameObject.widthPx
        val y2 = y + gameObject.heightPx
        if (gameObject.javaClass == SolidBlock::class.java) {
            g.color = Color.BLACK
            g.fillPolygon(intArrayOf(x, x2, x), intArrayOf(y, y, y2), 3)
            g.color = Color.LIGHT_GRAY
            g.fillPolygon(intArrayOf(x, x2, x2), intArrayOf(y2, y, y2), 3)
        } else if (gameObject.javaClass == Player::class.java) {
            g.color = Color.BLUE
            g.fillRect(x, y, gameObject.widthPx, gameObject.heightPx)
        }
    }

    fun repaint() {
        try {
            val g = panel.graphics
            if ((g != null && dbImage != null))
                g.drawImage(dbImage, 0, 0, null)
            Toolkit.getDefaultToolkit().sync()
            g.dispose()
        } catch (e: Exception) {
            println("Repaint error: " + e)
        }
    }
}