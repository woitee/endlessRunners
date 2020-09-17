package cz.woitee.endlessRunners.evolution

import cz.woitee.endlessRunners.evolution.charts.ChartData
import cz.woitee.endlessRunners.evolution.charts.MultiCharter

class EvoProgressAccumulator {
    data class Entry(val gid: Int, val id: Int, val key: String, val bestFitness: Double)

    val allEntries = ArrayList<Entry>()
    val entriesByKey = mutableMapOf<String, ArrayList<Entry>>()

    val charter = MultiCharter()

    private var lastKey = ""
    private val keyToNumGenerations = HashMap<String, ArrayList<Int>>()
    private var currentNumGenerations = 0

    fun addData(key: String, bestFitness: Double) {
        if (!entriesByKey.containsKey(key)) entriesByKey[key] = arrayListOf()

        val entry = Entry(allEntries.size, entriesByKey[key]!!.size, key, bestFitness)

        allEntries.add(entry)
        entriesByKey[key]!!.add(entry)

        updateKeyCounting(key)

        charter.update(getChartDatas())
    }

    private fun updateKeyCounting(key: String) {
        if (key != lastKey && lastKey != "") {
            if (!keyToNumGenerations.containsKey(lastKey)) {
                keyToNumGenerations[lastKey] = arrayListOf(currentNumGenerations)
            } else {
                val list = keyToNumGenerations[lastKey]!!
                list.add(list.last() + currentNumGenerations)
            }
            currentNumGenerations = 0
        }
        lastKey = key
        ++currentNumGenerations
    }

    fun getChartDatas(): List<ChartData> {
        val differentKeys = entriesByKey.keys.map { it.split("-")[0] }.distinct()
        val charts = ArrayList<ChartData>()

        for (plotKey in differentKeys) {
            val lineNames = ArrayList<String>()
            val yDatas = ArrayList<List<Double>>()

            val secondaryKeys = ArrayList<String>()

            for ((key, list) in entriesByKey) {
                val split = key.split("-")
                if (split[0] != plotKey) continue

                val yData = list.map { it.bestFitness }
                yDatas.add(yData)
                lineNames.add(key)

                secondaryKeys.add(if (split.size > 1) split[1] else "")
            }

            val xData = List(yDatas[0].size) { it.toDouble() }
            val bestSecondaryKey = secondaryKeys.minOrNull()!!
            val bestKey = if (bestSecondaryKey == "") plotKey else "$plotKey-$bestSecondaryKey"

            charts.add(
                ChartData(
                    "Chart of $plotKey",
                    "generation",
                    "best fitness",
                    lineNames,
                    xData,
                    yDatas,
                    keyToNumGenerations.getOrDefault(bestKey, emptyList())
                )
            )
        }

        return charts
    }

    companion object {
        fun List<Double>.toPaddedDoubleArray(size: Int, padding: Double) = DoubleArray(size) { if (it < this.size) this[it] else padding }
    }
}
