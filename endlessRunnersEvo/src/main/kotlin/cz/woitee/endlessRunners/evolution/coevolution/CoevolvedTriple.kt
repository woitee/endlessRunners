package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock

/** A representation of a resulting triple of best game description, and the best controller and blocks for this description */
data class CoevolvedTriple(val blocks: ArrayList<HeightBlock>, val controller: EvolvedPlayerController, val description: EvolvedGameDescription)