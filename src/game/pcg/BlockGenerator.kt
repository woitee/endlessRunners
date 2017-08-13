package game.pcg

import game.GameState
import game.Grid2D
import game.HeightBlocks
import game.gameDescriptions.GameDescription
import game.gameObjects.GameObject
import utils.arrayList
import java.security.InvalidParameterException
import java.util.*

/**
 * A class to generate level from pre-saved 2D blocks.
 * Created by woitee on 13/08/2017.
 */
class BlockGenerator (val gameDescription: GameDescription, val mode: Mode = BlockGenerator.Mode.REPEAT): ILevelGenerator {
    enum class Mode {
        REPEAT,
        RANDOM
    }
    class Block(val width: Int, val height: Int) {
        val definition = Grid2D<GameObject?>(width, height, { null })

        constructor (gameDescription: GameDescription, stringBlock: List<String>): this(stringBlock[0].length, stringBlock.count()) {
            for (y in stringBlock.lastIndex.downTo(0)) {
                for (x in 0 .. width - 1) {
                    if (stringBlock[y][x] == 'P') {
                        throw InvalidParameterException("Player should not be part of a generator Block!")
                    }
                    definition[x, height - y - 1] = gameDescription.charToObject[stringBlock[y][x]]
                }
            }
        }
    }

    var blocks = arrayListOf(
        Block(gameDescription, arrayListOf(
  "                             ##    ",
            "                            ###    ",
            "      #    #  #   #       #####    ",
            "##                  ##   ######    ",
            "###################################"
        )),
        Block(gameDescription, arrayListOf(
  "                                 ",
            "                            #    ",
            "      #    #  #   #       ###    ",
            "##                  ##   ####    ",
            "#################################"
        )),
        Block(gameDescription, arrayListOf(
            "###########################"
        )),
        Block(gameDescription, arrayListOf(
            "            #                       ",
            "                                    ",
            "           #####                    ",
            "       #########     #              ",
            "     ############   ##            ##",
            "     ##################             ",
            "#######################           ##",
            "########################## #########",
            "####################################"
        )),
        Block(gameDescription, arrayListOf(
                "###########################"
        )),
        Block(gameDescription, arrayListOf(
            "            #                       ",
            "                                    ",
            "           #####                    ",
            "       #########     #              ",
            "     ############   ##            ##",
            "     ##################     # #     ",
            "#######################           ##",
            "########################## #########",
            "####################################"
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
        for (y in 0 .. HeightBlocks - 1) {
            if (y < block.height)
                col[y] = block.definition[currentBlockOffset, y]?.shallowCopy()
            else
                col[y] = null
        }
        currentBlockOffset++
        return col
    }

    override fun reset() {
        currentBlockIx = -1
        currentBlockOffset = 0
    }
}