package cz.woitee.experiments

import java.awt.Button
import java.awt.Color
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

class ExperimentGUI (val buttonLabels: Array<String>, val buttonCallbacks: Array<() -> Unit>) {
    val frame: JFrame = createFrame()
    lateinit var buttons: Array<Button>

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
        for (i in buttonLabels.indices) {
            val buttonLabel = buttonLabels[i]
            val buttonCallback = buttonCallbacks[i]
            val button = Button(buttonLabel)
            button.addActionListener {
                performCallbackInNewThread(buttonCallback)
            }
            panel.add(button)
        }
    }

    private fun performCallbackInNewThread(callback: () -> Unit) {
        hide()
        Thread({
            callback()
            SwingUtilities.invokeLater {
                show()
            }
        }).start()
    }

    fun show() {
        frame.isVisible = true
    }

    fun hide() {
        frame.isVisible = false
    }
}