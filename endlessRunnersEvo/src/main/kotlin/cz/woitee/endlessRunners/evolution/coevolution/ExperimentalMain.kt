package cz.woitee.endlessRunners.evolution.coevolution

fun main() {
    val coevolver = Coevolver(5)

    // 30, 50, 20
    coevolver.evolveBlocks(30, 1, 30,true)
    coevolver.saveToFile("out/tmp.tst")

    coevolver.evolveController(2, 50)
    print("Controller fitness: ${coevolver.controllerPopulation!!.bestFitness}")
}
