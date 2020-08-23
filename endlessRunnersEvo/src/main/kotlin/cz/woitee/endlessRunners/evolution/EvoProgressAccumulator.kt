package cz.woitee.endlessRunners.evolution

import cz.woitee.endlessRunners.evolution.charts.ChartData
import cz.woitee.endlessRunners.evolution.charts.MultiCharter

class EvoProgressAccumulator {
    data class Entry(val gid: Int, val id: Int, val key: String, val bestFitness: Double)

    val allEntries = ArrayList<Entry>()
    val entriesByKey = mutableMapOf<String, ArrayList<Entry>>()

    val charter = MultiCharter()

    fun addData(key: String, bestFitness: Double) {
        if (!entriesByKey.containsKey(key)) entriesByKey[key] = arrayListOf()

        val entry = Entry(allEntries.size, entriesByKey[key]!!.size, key, bestFitness)

        allEntries.add(entry)
        entriesByKey[key]!!.add(entry)

        charter.update(getChartDatas())
    }

    fun getChartDatas(): List<ChartData> {
        val differentKeys = entriesByKey.keys.map { it.split("-")[0] }.distinct()
        val charts = ArrayList<ChartData>()

        for (plotKey in differentKeys) {
            val lineNames = ArrayList<String>()
            val yDatas = ArrayList<List<Double>>()

            for ((key, list) in entriesByKey) {
                if (key.split("-")[0] != plotKey) continue

                val yData = list.map { it.bestFitness }
                yDatas.add(yData)
                lineNames.add(key)
            }

            val xData = List(yDatas[0].size) { it.toDouble() }

            charts.add(ChartData(
                    "Chart of $plotKey",
                    "generation",
                    "best fitness",
                    lineNames,
                    xData,
                    yDatas
            ))
        }

        return charts
    }

    companion object {
        fun List<Double>.toPaddedDoubleArray(size: Int, padding: Double) = DoubleArray(size) { if (it < this.size) this[it] else padding }
    }
}