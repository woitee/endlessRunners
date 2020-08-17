package cz.woitee.endlessRunners.evolution.grandEvo

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription

/**
 * A simple main function running a simple combined evolution of blocks and a player.
 */
fun main(args: Array<String>) {
    val gameDescription = CrouchGameDescription()
    val runner = GrandEvoRunner(gameDescription)

//    val block = runner.evolveBlockViaDoubles { DFSPlayerController(BasicDFS()) }
//    println(block)

//    val blocks = runner.evolveBlocks { DFSPlayerController(BasicDFS()) }
//    for (block in blocks) {
//        println(block)
//    }

    val (blocks, controller) = runner.evolveBlocksAndPlayer()
    val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController(controller.genotype) })
    evoBlockRunner.runGameWithBlocks(blocks)
}
