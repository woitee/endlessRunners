package cz.woitee.endlessRunners.experiments

import java.awt.Button
import java.awt.Color
import java.awt.TextArea
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.util.*
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider

/**
 * A GUI class for testing precision timing.
 */
class HitPrecisionTest {
    val defaultText = """
            Tato hra je na zjištění vaší preciznosti trefování správného okamžiku.
            Vpravo je posuvník. Vašim úkolem je stisknout "šipku nahoru"
            co nejpřesněji v moment, kdy dovystoupá na nápis "CÍL".

            Po deseti opakováních hra automaticky skončí.

            Nyní stiskněte tlačítko začít.
        """.trimIndent()
    val inGameText = """ Stiskněte klávesu "šipka nahoru" co nejpřesněji v okamžik,
        |kdy posuvník vystoupá na nápis "CÍL".
        |""".trimMargin()

    val textArea = TextArea(defaultText)
    val slider = JSlider(JSlider.VERTICAL, 0, 500, 0)
    val button = Button("Začít")
    val frame: JFrame = createFrame()

    val runLock = Object()
    val spacebarWaitLock = Object()

    val precisionTimes = ArrayList<Long>()

    var running = false

    var currentTimeTarget = -1L

    fun run() {
        frame.isVisible = true
        synchronized(runLock) {
            runLock.wait()
        }
        frame.isVisible = false
        frame.dispose()
    }

    private fun createFrame(): JFrame {
        val frame = JFrame("Experiment GUI")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pane = frame.contentPane

        val panel = JPanel()
        addContents(panel)

        panel.background = Color.DARK_GRAY

        panel.isFocusable = true
        panel.requestFocus()

        pane.add(panel)
        frame.pack()

        frame.isVisible = true

        return frame
    }

    private fun addContents(panel: JPanel) {
        button.addActionListener {
            Thread({
                runReactionTest()
            }).start()
        }
        textArea.isEditable = false

        panel.add(textArea)
        slider.isEnabled = false

        slider.majorTickSpacing = slider.maximum - slider.minimum
        slider.minorTickSpacing = slider.maximum - slider.minimum
        slider.paintTicks = true
        slider.paintLabels = true

        val dictionary = slider.labelTable
        for (key in dictionary.keys()) {
            val label = dictionary.get(key) as JLabel?
            if (label != null) {
                label.foreground = Color.WHITE
                if (key == slider.maximum) {
                    label.text = "CÍL"
                } else {
                    label.text = ""
                }
            }
        }

        panel.add(slider)
        panel.add(button)

        panel.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == 38) {
                    spaceBarPressed()
                }
            }
        })
    }

    private fun spaceBarPressed() {
        val hitPrecisionNano = System.nanoTime() - currentTimeTarget
        val hitPrecisionMillis = hitPrecisionNano / 1000000
        println("HitPrecision $hitPrecisionMillis ms")

        precisionTimes.add(hitPrecisionNano)

        synchronized(spacebarWaitLock) {
            currentTimeTarget = -1L
            spacebarWaitLock.notify()
        }
    }

    /**
     * Runs the whole test and saves log.
     */
    private fun runReactionTest() {
        if (running)
            return
        running = true

        button.isEnabled = false
        textArea.text = inGameText

        Thread.sleep(3000)
        for (i in 1..10) {
            val timeStart = System.nanoTime()
            var timeNow = System.nanoTime()
            currentTimeTarget = timeNow + 2L * 1000L * 1000L * 1000L // 3 seconds

            println("Setting currentTimeTarget $currentTimeTarget")

            val sleepTime = 10L
            while (currentTimeTarget != -1L && timeNow < currentTimeTarget) {
                Thread.sleep(sleepTime)
                timeNow = System.nanoTime()

                val proportion = (timeNow - timeStart).toDouble() / (currentTimeTarget - timeStart)
                slider.value = Math.round(proportion * (slider.maximum - slider.minimum)).toInt()
            }

            synchronized(spacebarWaitLock) {
                if (currentTimeTarget != -1L)
                    spacebarWaitLock.wait()
            }

            slider.value = slider.minimum
            Thread.sleep(1000)
        }

        textArea.text = """A to je vše! Díky!
            |
            |Vracím se do menu...
        """.trimMargin()
        Thread.sleep(3000)

        synchronized(runLock) {
            runLock.notify()
        }
    }

    private fun saveLog(filename: String) {
        println("Saving reactionTest log to file $filename")
        val file = File(filename)
        val writer = file.bufferedWriter()
        for (time in precisionTimes) {
            writer.write("$time")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }
}
