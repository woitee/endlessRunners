package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.descriptions.imitators.ChameleonGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock

/**
 * Returns the default blocks for Chameleon.
 */
fun chameleonGameDefaultBlocks(gameDescription: ChameleonGameDescription): ArrayList<HeightBlock> {
    return arrayListOf(
            HeightBlock(gameDescription, arrayListOf(
                    "P       P",
                    "P       P",
                    "#########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P       P",
                    "P       P",
                    "000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                     P",
                    "P                     P",
                    "#########     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                     P",
                    "P                     P",
                    "#########     #########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                     P",
                    "P                     P",
                    "000000000     #########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                     P",
                    "P                     P",
                    "000000000     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                      ",
                    "P                     P",
                    "#########             P",
                    "#########     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                      ",
                    "P                     P",
                    "000000000             P",
                    "000000000     #########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                      ",
                    "P                     P",
                    "#########             P",
                    "#########     #########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "P                      ",
                    "P                     P",
                    "000000000             P",
                    "000000000     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "                      P",
                    "P                     P",
                    "P             000000000",
                    "#########     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "                      P",
                    "P                     P",
                    "P             #########",
                    "000000000     #########"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "                      P",
                    "P                     P",
                    "P             000000000",
                    "000000000     000000000"
            )),
            HeightBlock(gameDescription, arrayListOf(
                    "                      P",
                    "P                     P",
                    "P             #########",
                    "#########     #########"
            )))
}
