package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock

/**
 * Returns the default blocks for Chameleon.
 */
fun chameleonGameDefaultBlocks(gameDescription: GameDescription): ArrayList<HeightBlock> {
    return arrayListOf(
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "#########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                     P",
                "P                     P",
                "#########     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                     P",
                "P                     P",
                "#########     #########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                     P",
                "P                     P",
                "000000000     #########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                     P",
                "P                     P",
                "000000000     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                      ",
                "P                     P",
                "#########             P",
                "#########     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                      ",
                "P                     P",
                "000000000             P",
                "000000000     #########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                      ",
                "P                     P",
                "#########             P",
                "#########     #########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P                      ",
                "P                     P",
                "000000000             P",
                "000000000     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "                      P",
                "P                     P",
                "P             000000000",
                "#########     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "                      P",
                "P                     P",
                "P             #########",
                "000000000     #########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "                      P",
                "P                     P",
                "P             000000000",
                "000000000     000000000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "                      P",
                "P                     P",
                "P             #########",
                "#########     #########"
            )
        )
    )
}

fun slowChameleonGameDefaultBlocks(gameDescription: GameDescription): ArrayList<HeightBlock> {
    return arrayListOf(
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "#########"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "000000000",
            )
        ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
            HeightBlock(
                    gameDescription,
                    arrayListOf(
                            "P       P",
                            "P       P",
                            "000000000",
                    )
            ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "###   000",
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "###   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "000   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P       P",
                "P       P",
                "000   000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P        ",
                "P       P",
                "###     P",
                "###   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P        ",
                "P       P",
                "000     P",
                "###   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P        ",
                "P       P",
                "###     P",
                "###   000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P        ",
                "P       P",
                "000     P",
                "###   000"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "        P",
                "P       P",
                "P     ###",
                "###   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "        P",
                "P       P",
                "P     ###",
                "000   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "        P",
                "P       P",
                "P     000",
                "###   ###"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "        P",
                "P       P",
                "P     000",
                "000   ###"
            )
        )
    )
}
