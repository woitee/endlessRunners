package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock

/**
 * Default blocks for the BitTri game.
 */
fun bitTriGameDefaultBlocks(gameDescription: GameDescription): ArrayList<HeightBlock> {
    return arrayListOf(
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P  P",
                "P  P",
                "####"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P     P",
                "P     P",
                "##   ##"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P      P",
                "P  1   P",
                "########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "       P",
                "P      P",
                "P   ####",
                "########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P      ",
                "P     P",
                "###   P",
                "#######"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "####### ",
                " #####  ",
                "P ###  P",
                "P      P",
                "########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P  3  P",
                "P  3  P",
                "#######"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "        P",
                "        P",
                "      ###",
                "P     ###",
                "P     ###",
                "##0######"
            )
        )
    )
}
