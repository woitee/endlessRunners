package cz.woitee.endlessRunners.game.gui

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.geom.Vector2Double
import java.awt.*
import java.awt.event.KeyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * Created by woitee on 15/01/2017.
 */

open class GamePanelVisualizer(val panelName: String = "Endless Runners GUI", var shouldEndGameOnStop: Boolean = true, val debugging: Boolean = false) : GameVisualizerBase() {
    lateinit var frame: JFrame
        private set
    var panel: JPanel = JPanel(BorderLayout())
        private set
    private var dbg: Graphics? = null
    private var dbImage: Image? = null

    private var running = false
    protected val announcer = GamePanelAnnouncerComponent()

    private var proportionOfUpdate = 0.0

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
        SwingUtilities.invokeAndWait swingThread@{
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

    final override fun addKeyListener(listener: KeyListener) {
        panel.addKeyListener(listener)
    }

    open fun drawEverything(gameState: GameState, g: Graphics) {
        val timeSinceLastUpdate = System.nanoTime() - gameState.game.updateThread.lastUpdateAt.get()
        val updateEveryMicros = 1000000L / gameState.game.updateRate
        proportionOfUpdate = (((timeSinceLastUpdate / 1000).toDouble() / updateEveryMicros) - 0.3).coerceIn(0.0, 1.0)

        if (gameState.game.secondsSinceStart < gameState.game.freezeOnStartSeconds)
            proportionOfUpdate = 0.0

        val playerX = proportionedX(gameState.player)

        for (gameObject in gameState.gameObjects)
            drawGameObject(gameObject, g, playerX)

        announcer.draw(g, gameState)

        if (debugging)
            for (debugObject in debugObjects)
                drawGameObject(debugObject, g, playerX)
    }

    fun announce(message: String) {
        announcer.startAnnouncing(message)
    }

    fun drawGameObject(gameObject: GameObject, g: Graphics, playerX: Double) {
        val topLeftX = (proportionedX(gameObject) - playerX + PlayerScreenX).toInt()
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
            println("Repaint error: " + e + "\n" + e.stackTrace)
        }
    }

    /**
     * These functions resolve uglyness at low update speeds.
     *
     * They approximate the position of objects from the updateThread's last update time (by immediate velocity).
     */
    protected fun proportionedX(gameObject: GameObject): Double {
        if (gameObject !is MovingObject)
            return gameObject.x

        val updateTime = gameObject.gameState.game.updateTime
        return gameObject.x + gameObject.xspeed * updateTime * BlockWidth * proportionOfUpdate
    }

    protected fun proportionedY(gameObject: GameObject): Double {
        if (gameObject !is MovingObject)
            return gameObject.y

        val updateTime = gameObject.gameState.game.updateTime
        return gameObject.y + gameObject.yspeed * updateTime * BlockWidth * proportionOfUpdate
    }

    protected fun proportionedLocation(gameObject: GameObject): Vector2Double {
        return Vector2Double(proportionedX(gameObject), proportionedY(gameObject))
    }
}
