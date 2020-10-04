package cz.woitee.endlessRunners.evolution.utils

import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import cz.woitee.endlessRunners.utils.fileWithCreatedPath
import io.jenetics.DoubleGene
import io.jenetics.Gene
import io.jenetics.engine.EvolutionResult
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileWriter
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collector
import java.util.stream.Stream

/**
 * A CSV recording utility that prints stats from each generation of an evolutionary run into a CSV file.
 * Usable as argument in the EvolutionStream.peek() method.
 *
 * @param filepathBase Start of the name of the file, it will be compounded with current timestamp (to be able to rerun this method).
 * @param serializeWholePopulation Whether to include serializations of complete populations (can take a lot of disk space).
 */
class CSVPrintingPeeker<C> (val filepathBase: String, val serializeWholePopulation: Boolean = false) : Consumer<EvolutionResult<*, C>>
        where C : Comparable<C> {

    val file = fileWithCreatedPath("${filepathBase}_${DateUtils.timestampString()}.csv")
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
            DateUtils.timestampString(),
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

/** Collects the evolution stream using a given collector, noting the results into CSV */
fun <G : Gene<*, G>?, T: Comparable<T>> Stream<EvolutionResult<G, T>>.collectWithCSVPeeker(
        collector: Collector<EvolutionResult<G, T>, *, EvolutionResult<G, T>>,
        filepathBase: String): EvolutionResult<G, T> {

    val csvPeeker = CSVPrintingPeeker<T>(filepathBase)
    return peek(csvPeeker).collect(collector).also { csvPeeker.close() }
}
