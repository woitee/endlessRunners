package cz.woitee.endlessRunners.evolution.charts

import org.knowm.xchart.XYChart

data class ChartData(val title: String,
                     val xTitle: String,
                     val yTitle: String,
                     val lineNames: List<String>,
                     val xData: List<Number>,
                     val yData: List<List<Number>>) {

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
                xyChart.addSeries(lineName, xs, ys)
            }
        }
    }
}

fun XYChart.update(chartData: ChartData) {
    chartData.updateXYChart(this)
}