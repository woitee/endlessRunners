package cz.woitee.experiments

import java.awt.Button
import java.awt.Color
import java.awt.Label
import java.awt.TextArea
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel

class ReactionTest {
    val defaultText = """
            Jednoduchá hra na zjištění doby odezvy.
            Vpravo je malý červený obdélník. Vašim úkolem je stisknout "šipku nahoru",
            vždy, jakmile změní barvu na zelenou.
            Po deseti opakováních hra automaticky skončí.

            Nyní stiskněte tlačítko začít.
        """.trimIndent()
    val failText = """
            Ups.
            Stiskli jste klávesu "šipka nahoru" dříve, než obdélník zezelenal.

            Začněte prosím znovu.
        """.trimIndent()
    val waitNowText = """Čekejte... """
    val pressNowText = """Zmáčkněte klávesu "šipka nahoru"! """

    val textArea = TextArea(defaultText)
    val colorLight = Label()
    val button = Button("Začít")
    val frame: JFrame = createFrame()
    val isLightOn
        get() = colorLight.background == Color.GREEN

    val runLock = Object()
    val spacebarWaitLock = Object()

    val reactionTimes = ArrayList<Long>()
    var lastLightOnTime: Long = System.nanoTime()

    var running = false

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
        colorLight.background = Color.RED
        button.addActionListener {
            Thread({
                runReactionTest()
            }).start()
        }
        textArea.isEditable = false

        panel.add(textArea)
        panel.add(colorLight)
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
        if (!isLightOn) {
            restart()
        } else {
            turnOffLight()
            synchronized(spacebarWaitLock) {
                spacebarWaitLock.notify()
            }
        }
    }

    private fun restart() {
        button.isEnabled = true
        textArea.text = failText
    }

    private fun turnOnLight() {
        colorLight.background = Color.GREEN
        textArea.text = pressNowText
        lastLightOnTime = System.nanoTime()
    }

    private fun turnOffLight() {
        colorLight.background = Color.RED
        textArea.text = waitNowText
        val reactionTimeNano = System.nanoTime() - lastLightOnTime
        println("Reaction in ${reactionTimeNano / 1000000} ms")
        reactionTimes.add(reactionTimeNano)
    }

    private fun runReactionTest() {
        if (running)
            return
        running = true

        button.isEnabled = false
        textArea.text = waitNowText

        val random = Random()

        Thread.sleep(3000)
        for (i in 1 .. 10) {
            Thread.sleep(1000 + random.nextInt(3000).toLong())
            synchronized(spacebarWaitLock) {
                if (button.isEnabled) {
                    running = false
                    return
                }
                turnOnLight()
                spacebarWaitLock.wait()
                // check if user made mistake
                if (button.isEnabled) {
                    running = false
                    return
                }
            }
        }

        textArea.text = "A to je vše! Díky!"
        colorLight.isVisible = false
        button.isVisible = false
        saveLog("ReactionTest_${SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())}.log")
        Thread.sleep(3000)

        synchronized(runLock) {
            runLock.notify()
        }
    }

    private fun saveLog(filename: String) {
        println("Saving reactionTest log to file $filename")
        val file = File(filename)
        val writer = file.bufferedWriter()
        for (time in reactionTimes) {
            writer.write("$time")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }
}