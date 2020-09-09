package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock

fun impossibleGameDefaultBlocks(gameDescription: GameDescription): ArrayList<HeightBlock> {
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
                "P  1  P",
                "#######"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P      P",
                "P  11  P",
                "########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "    P",
                "P   P",
                "P   #",
                "#    "
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "    P",
                "P   P",
                "P   #",
                "#    "
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "    P",
                "P   P",
                "P   #",
                "#    "
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P    ",
                "P    ",
                "#   P",
                "    P",
                " ####"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P     ",
                "P    P",
                "#    P",
                "     #",
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "       ",
                "P      ",
                "P     P",
                "#111  P",
                "#######"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "     ",
                "    P",
                "P   P",
                "P  1#",
                "#####"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P   ",
                "P  P",
                "#  P",
                "   #"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P     ",
                "P 11 P",
                "#### P",
                "     #"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P     ",
                "P 1  P",
                "###  P",
                "     #",
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P      ",
                "P      ",
                "#      ",
                "      P",
                "      P",
                "      #",
            )
        ),
    )
}
