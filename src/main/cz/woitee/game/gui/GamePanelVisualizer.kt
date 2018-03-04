package cz.woitee.game.gui

import cz.woitee.game.*
import cz.woitee.game.objects.*
import java.awt.*
import java.awt.event.KeyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * Created by woitee on 15/01/2017.
 */

open class GamePanelVisualizer(val panelName: String = "Endless Runners GUI", var shouldEndGameOnStop: Boolean = true, val debugging:Boolean = false): GameVisualizerBase() {
    lateinit var frame: JFrame
        private set
    var panel: JPanel = JPanel(BorderLayout())
        private set
    private var dbg: Graphics? = null
    private var dbImage: Image? = null

    private var running = false

    // Not a part of the game, can be used to add debug information
    val debugObjects = ArrayList<GameObject>()

    init {
        init()
    }

    final override fun init() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeAndWait {
                frame = createFrame()
            }
        } else {
            frame = createFrame()
        }

        running = true
    }

    private fun createFrame(): JFrame {
        val frame = JFrame(panelName)
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                dispose()
            }
        })
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

    final override fun dispose() {
        SwingUtilities.invokeLater {
            frame.isVisible = false
        }
        running = false

        dbg?.dispose()
        frame.dispose()
    }

    override fun update(gameState: GameState) {
        if (!running) {
            if (shouldEndGameOnStop) {
                stopGame(gameState)
            }
            return
        }
        SwingUtilities.invokeAndWait swingThread@ {
            if (dbImage == null) {
                dbImage = panel.createImage(GameWidth, GameHeight)
                if (dbImage == null) {
                    println("DBImage is null")
                    dispose()
                    return@swingThread
                }
                dbg = dbImage!!.graphics
            }

            val _dbg = dbg!!
            _dbg.color = Color.WHITE
            _dbg.fillRect(0, 0, GameWidth, GameHeight)

            synchronized(gameState.gameObjects) {
                drawEverything(gameState, _dbg)
            }
            frame.title = "$panelName Score:${gameState.gridX / 10}"

            repaint()
        }
    }

    override final fun addKeyListener(listener: KeyListener) {
        panel.addKeyListener(listener)
    }

    open fun drawEverything(gameState: GameState, g: Graphics) {
        val playerX = gameState.player.x

        for (gameObject in gameState.gameObjects)
            drawGameObject(gameObject, g, playerX)

        if (debugging)
            for (debugObject in debugObjects)
                drawGameObject(debugObject, g, playerX)
    }

    fun drawGameObject(gameObject: GameObject, g: Graphics, playerX: Double) {
        val topLeftX = (gameObject.x - playerX + PlayerScreenX).toInt()
        val topLeftY = GameHeight - gameObject.y.toInt() - gameObject.heightPx
        drawGameObjectAt(gameObject, g, topLeftX, topLeftY)
    }

    open fun drawGameObjectAt(gameObject: GameObject, g: Graphics, x: Int, y: Int) {
        val x2 = x + gameObject.widthPx
        val y2 = y + gameObject.heightPx
        if (gameObject.gameObjectClass == GameObjectClass.SOLIDBLOCK) {
            g.color = Color.BLACK
            g.fillPolygon(intArrayOf(x, x2, x), intArrayOf(y, y, y2), 3)
            g.color = Color.LIGHT_GRAY
            g.fillPolygon(intArrayOf(x, x2, x2), intArrayOf(y2, y, y2), 3)
        } else {
            g.color = gameObject.color.awtColor
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