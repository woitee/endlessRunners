package cz.woitee.endlessRunners.experiments

import cz.woitee.endlessRunners.utils.arrayList
import java.awt.Button
import java.awt.Color
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * A complete GUI object, created for running the Experiment. It is formed as a multi-button panel, that runs other given actionns.
 *
 * @param buttonLabels Array of Strings that should be used for button labels.
 * @param buttonCallbacks Array of Functions that execute when the appropriate buttons are clicked.
 * @param generatedLogPatterns Patterns of logs that should cause the individual buttons to not show as enabled anymore.
 */
class ExperimentGUI(val buttonLabels: Array<String>, val buttonCallbacks: Array<() -> Boolean>, val generatedLogPatterns: Array<String>) {
    val frame: JFrame = createFrame()
    lateinit var buttons: ArrayList<Button>
    val permanentlyDone = arrayList(4, { false })

    private fun createFrame(): JFrame {
        val frame = JFrame("Experiment GUI")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pane = frame.contentPane

        val panel = JPanel()
        createButtons(panel)

        panel.background = Color.DARK_GRAY
        panel.isVisible = true

        panel.isFocusable = true
        panel.requestFocus()

        pane.add(panel)
        frame.pack()

        return frame
    }

    private fun createButtons(panel: JPanel) {
        buttons = ArrayList()
        for (i in buttonLabels.indices) {
            val buttonLabel = buttonLabels[i]
            val buttonCallback = buttonCallbacks[i]
            val button = Button(buttonLabel)
            button.addActionListener {
                performCallbackInNewThread(i, buttonCallback)
            }
            panel.add(button)
            buttons.add(button)
        }
    }

    private fun performCallbackInNewThread(i: Int, callback: () -> Boolean) {
        hide()
        Thread({
            if (callback()) {
                permanentlyDone[i] = true
            }
            SwingUtilities.invokeLater {
                show()
            }
        }).start()
    }

    fun show() {
        val fileNames = File(".").listFiles().map { it.name }

        frame.isVisible = true
        for (i in buttons.indices) {
            val button = buttons[i]
            val logPattern = generatedLogPatterns[i]

            if (permanentlyDone[i]) button.isEnabled = false

            if (logPattern != "") {
                val logFileExists = fileNames.any { it.matches(Regex(logPattern)) }

                button.isEnabled = !logFileExists
            }
        }
    }

    fun hide() {
        frame.isVisible = false
    }
}
