package cz.woitee.endlessRunners.evolution.charts

import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XChartPanel
import org.knowm.xchart.XYChart
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

class MultiCharter {
    val chartPanels = ArrayList<XChartPanel<XYChart>>()
    val charts = ArrayList<XYChart>()

    val frame: JFrame by lazy {
        val frame = JFrame("MultiCharter")
        javax.swing.SwingUtilities.invokeLater {
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.contentPane.layout = GridLayout(2, 2)
        }
        frame
    }

    fun update(chartDatas: List<ChartData>) {
        var addedNewChart = false

        for ((i, chartData) in chartDatas.withIndex()) {
            if (i < charts.size) {
                charts[i].update(chartData)
            } else {
                val chart = chartData.toXYChart()
                charts.add(chart)
                addedNewChart = true
            }
        }

        if (addedNewChart) {
            repackJFrame()
        }
        frame.repaint()
    }

    private fun repackJFrame() {
        if (chartPanels.size >= charts.size) return

        javax.swing.SwingUtilities.invokeLater {
            for (i in chartPanels.size until charts.size) {
                val chartPanel = XChartPanel<XYChart>(charts[i])
                chartPanels.add(chartPanel)
                frame.add(chartPanel)
            }

            frame.pack()
            frame.isVisible = true
        }
    }
}