package cz.woitee.endlessRunners.evolution.charts

import org.knowm.xchart.XChartPanel
import org.knowm.xchart.XYChart
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

class MultiCharter {
    val chartPanels = ArrayList<XChartPanel<XYChart>>()
    val additionalPanels = ArrayList<JPanel>()
    var additionalPanelsUpdated = false

    val charts = ArrayList<XYChart>()

    val frame: JFrame by lazy {
        val frame = JFrame("MultiCharter")
        javax.swing.SwingUtilities.invokeLater {
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.contentPane.layout = GridLayout(1, 2)
        }
        frame
    }

    fun update(chartDatas: List<ChartData>) {
        var addedNewChart = false

        for ((i, chartData) in chartDatas.withIndex()) {
            if (i >= charts.size) {
                charts.add(XYChart(600, 400))
                addedNewChart = true
            }

            val chart = charts[i]
            chart.update(chartData)
        }

        if (addedNewChart) {
            repackJFrame()
        }

        for (chart in charts) {
            for (series in chart.seriesMap.values) {
                if (series.xySeriesRenderStyle == null) return
            }
        }

        frame.repaint()
    }

    private fun repackJFrame() {
        if (chartPanels.size >= charts.size && !additionalPanelsUpdated) return
        additionalPanelsUpdated = false

        javax.swing.SwingUtilities.invokeLater {
            for (i in chartPanels.size until charts.size) {
                val chartPanel = XChartPanel(charts[i])
                chartPanels.add(chartPanel)
            }

            chartPanels.forEach { frame.add(it) }
            additionalPanels.forEach { frame.add(it) }

            val currentLayout = frame.contentPane.layout
            if (currentLayout is GridLayout) {
                currentLayout.rows = if (chartPanels.size <= 1) 1 else 2
            }

            frame.pack()
            frame.isVisible = true
        }
    }

    fun pushAdditionalPanel(panel: JPanel) {
        additionalPanels.add(panel)
        additionalPanelsUpdated = true
        repackJFrame()
    }

    fun popAdditionalPanel(): JPanel {
        val panel = additionalPanels.removeLast()
        frame.remove(panel)
        additionalPanelsUpdated = true
        repackJFrame()
        return panel
    }
}
