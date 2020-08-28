package cz.woitee.endlessRunners.game.gui

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.objects.*
import cz.woitee.endlessRunners.geom.Vector2Double
import java.awt.*
import java.awt.event.KeyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * The basic visualization of the game. It was implemented using methods from the Killer Game Programming in Java book.
 *
 * @param panelName Name of the panel
 * @param shouldEndGameOnStop Whether the game should stop or continue when closing this panel
 * @param height Height of the panel in pixels
 * @param width Width of the panel in pixels
 * @param playerScreenX X - position of the player on the screen
 * @param timeProportionedDrawing Whether we approximate the position of objects based on their velocity, this
 *        can improve drawing quality when the update rate is lower than visualize rate
 * @param showFrame If the frame is immediately visible
 * @param debugging Whether we draw debug objects.
 */

open class GamePanelVisualizer(
    val panelName: String = "Endless Runners GUI",
    var shouldEndGameOnStop: Boolean = true,
    val height: Int = GameHeight,
    val width: Int = GameWidth,
    val playerScreenX: Double = PlayerScreenX,
    val timeProportionedDrawing: Boolean = true,
    val showFrame: Boolean = true,
    val debugging: Boolean = false
) : GameVisualizerBase() {
    lateinit var frame: JFrame
        private set
    var panel: JPanel = JPanel(BorderLayout())
        private set
    private var dbg: Graphics? = null
    private var dbImage: Image? = null

    private var running = false
    protected val announcer = GamePanelAnnouncerComponent()
    val debugText = DebugTextComponent()

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
        frame.addWindowListener(
            object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    dispose()
                }
            }
        )
        val pane = frame.contentPane

        panel.background = Color.RED
        panel.preferredSize = Dimension(width, height)
        panel.isVisible = true

        panel.isFocusable = true
        panel.requestFocus()

        pane.add(panel)

        frame.pack()
        if (showFrame) frame.isVisible = true

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
                dbImage = panel.createImage(width, height)
                if (dbImage == null) {
                    println("DBImage is null")
                    dispose()
                    return@swingThread
                }
                dbg = dbImage!!.graphics
            }

            val _dbg = dbg!!
            _dbg.color = Color.WHITE
            _dbg.fillRect(0, 0, width, height)

            synchronized(gameState.gameObjects) {
                drawEverything(gameState, _dbg)
            }
            frame.title = "$panelName Score:${gameState.gridX + gameState.score}"

            repaint()
        }
    }

    final override fun addKeyListener(listener: KeyListener) {
        panel.addKeyListener(listener)
    }

    open fun drawEverything(gameState: GameState, g: Graphics) {
        val timeSinceLastUpdate = System.nanoTime() - gameState.game.updateThread.lastUpdateAt.get()
        val updateEveryMicros = 1000000L / gameState.game.updateRate
        if (gameState.game.running)
            proportionOfUpdate = (((timeSinceLastUpdate / 1000).toDouble() / updateEveryMicros) - 0.3).coerceIn(0.0, 1.0)

        if (gameState.game.secondsSinceStart < gameState.game.freezeOnStartSeconds)
            proportionOfUpdate = 0.0

        val playerX = proportionedX(gameState.player)

        for (gameObject in gameState.gameObjects)
            drawGameObject(gameObject, g, playerX)

        announcer.draw(g, gameState)
        debugText.draw(g)

        if (debugging)
            for (debugObject in debugObjects)
                drawGameObject(debugObject, g, playerX, true)
    }

    fun announce(message: String) {
        announcer.startAnnouncing(message)
    }

    protected fun drawGameObject(gameObject: GameObject, g: Graphics, playerX: Double, debug: Boolean = false) {
        val topLeftX = (proportionedX(gameObject) - playerX + playerScreenX).toInt()
        val topLeftY = height - gameObject.y.toInt() - gameObject.heightPx
        drawGameObjectAt(gameObject, g, topLeftX, topLeftY, debug)
    }

    open fun drawGameObjectAt(gameObject: GameObject, g: Graphics, x: Int, y: Int, debug: Boolean = false) {
        val x2 = x + gameObject.widthPx
        val y2 = y + gameObject.heightPx
        if (gameObject.gameObjectClass == GameObjectClass.SOLIDBLOCK) {
            g.color = Color.BLACK
            g.fillPolygon(intArrayOf(x, x2, x), intArrayOf(y, y, y2), 3)
            g.color = Color.LIGHT_GRAY
            g.fillPolygon(intArrayOf(x, x2, x2), intArrayOf(y2, y, y2), 3)
        } else {
            g.color = gameObject.color.awtColor
            if (debug) {
                g.color = Color(g.color.red, g.color.green, g.color.blue, 127)
            }
            g.fillRect(x, y, gameObject.widthPx, gameObject.heightPx)
        }
    }

    /**
     * Main function that repaints the screen.
     */
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
     * This approximates the "immediate" x position of an object, based on it's immediate velocity and time since last game-update.
     * This then improves the drawing quality, when redraw speed is faster than game-update speeds.
     */
    protected fun proportionedX(gameObject: GameObject): Double {
        if (!timeProportionedDrawing || gameObject !is MovingObject)
            return gameObject.x

        val updateTime = gameObject.gameState.game.updateTime
        return gameObject.x + gameObject.xspeed * updateTime * BlockWidth * proportionOfUpdate
    }

    /**
     * This approximates the "immediate" y position of an object, based on it's immediate velocity and time since last game-update.
     * This then improves the drawing quality, when redraw speed is faster than update speeds.
     */
    protected fun proportionedY(gameObject: GameObject): Double {
        if (!timeProportionedDrawing || gameObject !is MovingObject)
            return gameObject.y

        val updateTime = gameObject.gameState.game.updateTime
        return gameObject.y + gameObject.yspeed * updateTime * BlockWidth * proportionOfUpdate
    }

    /**
     * This approximates the "immediate" location of an object, based on it's immediate velocity and time since last game-update.
     * This then improves the drawing quality, when redraw speed is faster than update speeds.
     */
    protected fun proportionedLocation(gameObject: GameObject): Vector2Double {
        return Vector2Double(proportionedX(gameObject), proportionedY(gameObject))
    }
}
