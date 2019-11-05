package cz.woitee.endlessRunners.evolution.evoController

import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.SimpleLevelGenerator

/**
 * A simple main method running neuroevolution on the Crouch game.
 */
fun main(args: Array<String>) {

    val gameDescription = CrouchGameDescription()
    val runner = EvoControllerRunner(gameDescription, { SimpleLevelGenerator() })
    val controller = runner.evolveController()

    runner.runGame(controller)
}
