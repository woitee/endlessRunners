package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.coevolution.evolved.CoevolvedTriples

/**
 * A main method to show short examples of the coevolutionary results in a sequence.
 */
fun main(args: Array<String>) {
    val runner = CoevolutionRunner()
    for (i in 0..19) {
        val triple = CoevolvedTriples.get(i)
        println("This is the result of run $i")
        println(triple.description)
        runner.runGame(triple, 30.0)
    }
}
