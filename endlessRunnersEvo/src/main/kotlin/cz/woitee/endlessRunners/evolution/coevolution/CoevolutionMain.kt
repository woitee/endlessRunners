package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.coevolution.evolved.CoevolvedTriples
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.utils.fileWithCreatedPath

fun main(args: Array<String>) {
    val runner = CoevolutionRunner()
    val triple = runner.coevolveDescriptionBlocksAndController()

    println(triple.description)
    runner.runGame(triple)

    println(triple.description)
}