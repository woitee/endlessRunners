package cz.woitee.endlessRunners.evolution.charts

import java.awt.BasicStroke
import java.awt.Color
import javax.swing.SwingUtilities
import org.knowm.xchart.XYChart
import org.knowm.xchart.XYSeries

data class ChartData(
    val title: String,
    val xTitle: String,
    val yTitle: String,
    val lineNames: List<String>,
    val xData: List<Number>,
    val yData: List<List<Number>>,
    val xMarksEvery: Int = 50
) {

    fun toXYChart(): XYChart {
        val xyChart = XYChart(600, 400)
        xyChart.update(this)

        return xyChart
    }

    fun updateXYChart(xyChart: XYChart) {
        xyChart.title = title
        xyChart.xAxisTitle = xTitle
        xyChart.yAxisTitle = yTitle

        for (i in lineNames.indices) {
            val lineName = lineNames[i]
            val ys = yData[i].map { it.toDouble() }.toDoubleArray()
            // Some data series might still be incomplete - reduce xs to size of ys
            val xs = xData.map { it.toDouble() }.take(ys.size).toDoubleArray()

            if (xyChart.seriesMap.containsKey(lineNames[i])) {
                xyChart.updateXYSeries(lineName, xs, ys, null)
            } else {
                SwingUtilities.invokeAndWait {
                    val series = xyChart.addSeries(lineName, xs, ys)
                    series.xySeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
                    series.lineStyle = BasicStroke()
                    // Transparent markers
                    series.markerColor = Color(0, 0, 0, 0)
                }
            }
        }
        xyChart.setCustomXAxisTickLabelsMap(customTicksLabels())
    }

    private fun customTicksLabels(): MutableMap<Any, Any> {
        val map = HashMap<Any, Any>()

        for (i in 0 .. xData.size step xMarksEvery) {
            map[i] = i
        }

        return map
    }
}

fun XYChart.update(chartData: ChartData) {
    chartData.updateXYChart(this)
}
