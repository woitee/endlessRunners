package cz.woitee.endlessRunners.evolution.coevolution

fun main() {
    val runner = CoevolutionRunner()
    val triple = runner.coevolveDescriptionBlocksAndController()

    println(triple.description)
    runner.runGame(triple)

    println(triple.description)
}
