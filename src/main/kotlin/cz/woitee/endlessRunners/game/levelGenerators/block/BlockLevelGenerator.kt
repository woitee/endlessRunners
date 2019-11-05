package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.utils.arrayList

/**
 * A class to generate level from pre-saved 2D blocks.
 * Created by woitee on 13/08/2017.
 */
class BlockLevelGenerator(val gameDescription: GameDescription, val mode: Mode = Mode.REPEAT) : LevelGenerator() {
    enum class Mode {
        REPEAT,
        RANDOM
    }

    var blocks = arrayListOf(
            Block(gameDescription, arrayListOf(
                    "    ####################",
                    "########################"
            )),
            Block(gameDescription, arrayListOf(
                    "                      1        ",
                    "                      ##       ",
                    "            #      ###### #    ",
                    "               1############## ",
                    "            ###################",
                    "           0###################",
                    "##       ######################",
                    "###############################"
            ))
    )
    var currentBlockIx = -1
    var currentBlockOffset = 0

    protected fun selectNextBlock(gameState: GameState) {
        if (mode == Mode.REPEAT) {
            currentBlockIx++
            if (currentBlockIx >= blocks.size)
                currentBlockIx = 0
        } else if (mode == Mode.RANDOM) {
            currentBlockIx = gameState.game.random.nextInt(blocks.size)
        }
        currentBlockOffset = 0
    }

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (currentBlockIx < 0 || currentBlockOffset >= blocks[currentBlockIx].width)
            selectNextBlock(gameState)

        val block = blocks[currentBlockIx]
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        for (y in 0 until HeightBlocks) {
            if (y < block.height)
                col[y] = block.definition[currentBlockOffset, y]?.makeCopy()
            else
                col[y] = null
        }
        currentBlockOffset++
        return col
    }

    override fun init(gameState: GameState) {
        currentBlockIx = -1
        currentBlockOffset = 0
    }
}
