package cz.woitee.endlessRunners.experiments

import java.awt.Button
import java.awt.Color
import java.awt.Dimension
import java.awt.TextArea
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JPanel

class IntermediatoryDescriptorFrame(val description: String) {
    val frame = createFrame()
    val waitLock = Object()
    var returnSuccess = true

    fun createFrame(): JFrame {
        val frame = JFrame("Intermediatory Frame")
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                dispose()
            }
        })
        val panel = JPanel()

        addContent(panel)

        panel.background = Color.DARK_GRAY
        panel.isVisible = true

        panel.isFocusable = true
        panel.requestFocus()

        frame.contentPane.add(panel)
        frame.pack()

        return frame
    }

    fun dispose() {
        frame.dispose()
        synchronized(waitLock) {
            returnSuccess = false
            waitLock.notify()
        }
    }

    fun addContent(panel: JPanel) {
        val textArea = TextArea(description)
        textArea.preferredSize = Dimension(700, 200)
        panel.add(textArea)

        val continueButton = Button("Pokračovat")
        continueButton.addActionListener {
            hide()
            frame.dispose()
            synchronized(waitLock) {
                waitLock.notify()
            }
        }
        panel.add(continueButton)
    }

    fun show() {
        frame.isVisible = true
    }

    fun hide() {
        frame.isVisible = false
    }

    fun waitUntilInteraction(): Boolean {
        show()
        synchronized(waitLock) {
            waitLock.wait()
        }
        return returnSuccess
    }
}
