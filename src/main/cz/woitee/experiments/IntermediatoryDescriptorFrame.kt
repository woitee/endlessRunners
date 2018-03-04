package cz.woitee.experiments

import java.awt.Button
import java.awt.Color
import java.awt.Dimension
import java.awt.TextArea
import javax.swing.JFrame
import javax.swing.JPanel

class IntermediatoryDescriptorFrame(description: String) {
    val frame = createFrame()
    val waitLock = Object()

    fun createFrame(): JFrame {
        val frame = JFrame("Intermediatory Frame")
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
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

    fun addContent(panel: JPanel) {
        val textArea = TextArea(""" Právě spouštíte první hru. Ve hře ovládáte modrou postavu (obdélník),
která se sama pohybuje směrem vpravo. Při stisknutí klávesy "šipka nahoru" postava vyskočí,
a při stisknutí klávesy "šipka dolů" se skrčí. Vašim cílem je nenarazit do překážek a dostat se co nejdále.

Při naražení do překážky se hra restartuje a budete hrát opět od počátku - ale jinou úroveň.
Hra se sama ukončí po uplynutí pěti minut, prosím, neukončujte hru do té doby žádným způsobem.

Pokud je Vám vše jasné, klikněte na tlačítko "Pokračovat". Hra se ihned spustí.
        """)
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

    fun waitUntillInteraction() {
        show()
        synchronized(waitLock) {
            waitLock.wait()
        }
    }
}