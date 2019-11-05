package cz.woitee.endlessRunners.evolution.utils

import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import cz.woitee.endlessRunners.utils.fileWithCreatedPath
import io.jenetics.engine.EvolutionResult
import java.io.FileWriter
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

/**
 * A CSV recording utility that prints stats from each generation of an evolutionary run into a CSV file.
 * Usable as argument in the EvolutionStream.peek() method.
 *
 * @param filepathBase Start of the name of the file, it will be compounded with current timestamp (to be able to rerun this method).
 * @param serializeWholePopulation Whether to include serializations of complete populations (can take a lot of disk space).
 */
class CSVPrintingPeeker<C> (val filepathBase: String, val serializeWholePopulation: Boolean = false) : Consumer<EvolutionResult<*, C>>
        where C : Comparable<C> {

    val file = fileWithCreatedPath("${filepathBase}_${SimpleDateFormat("yyyy_MM_dd-HH_mm_ss_SSS").format(Date())}.csv")
    val fileWriter = FileWriter(file)
    val csvPrinter: CSVPrinter
    val header = arrayListOf("timestamp", "generation", "fitnesses", "bestindividual", "bestindividual_serialized", "bestfitness")

    init {
        // Create whole directory path for logfile
        if (serializeWholePopulation) {
            header.add("serialized")
        }
        csvPrinter = CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(*header.toTypedArray()))
    }

    /**
     * A method that get called every iteration.
     */
    override fun accept(result: EvolutionResult<*, C>) {
        val best = result.getBestPhenotype()
        val values = arrayListOf(
                Timestamp(System.currentTimeMillis()).time,
                result.getGeneration(),
                result.getPopulation().map { it.getFitness() },
                best.getGenotype(),
                JavaSerializationUtils.serializeToString(best.getGenotype()),
                best.getFitness()
        )
        if (serializeWholePopulation) {
            values.add(JavaSerializationUtils.serializeToString(result))
        }

        csvPrinter.printRecord(values)
        csvPrinter.flush()
    }

    /**
     * Close the underlying CSV stream.
     */
    fun close() {
        csvPrinter.close()
    }
}
