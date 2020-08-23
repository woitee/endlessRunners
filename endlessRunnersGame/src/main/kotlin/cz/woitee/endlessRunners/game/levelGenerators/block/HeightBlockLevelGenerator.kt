package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.utils.arrayList
import cz.woitee.endlessRunners.utils.randomElement
import java.util.ArrayList

/**
* A class to generate level from pre-saved 2D blocks, by matching their ending and starting heights.
*/
open class HeightBlockLevelGenerator(
    val gameDescription: GameDescription,
    val blocks: List<HeightBlock> =
        arrayListOf(
                HeightBlock(gameDescription, arrayListOf(
                        "P  P",
                        "P  P",
                        "####"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "       P",
                        "P      P",
                        "P   ####",
                        "########"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "P       ",
                        "P      P",
                        "####   P",
                        "########"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "P          ",
                        "P          ",
                        "####       ",
                        "####      P",
                        "####      P",
                        "###########"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "   ###   ",
                        "   ###   ",
                        "P  ###  P",
                        "P       P",
                        "#########"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "   ###        ",
                        "   ###        ",
                        "P  ###       P",
                        "P            P",
                        "#        #####",
                        "##############"
                )),
                HeightBlock(gameDescription, arrayListOf(
                        "      ",
                        "P     ",
                        "P    P",
                        "     P",
                        "    # ",
                        "######"
                ))
        )
) : LevelGenerator() {

    var currentBlockIx = -1
    var currentBlockOffset = 0
    var currentBlockY = 0
    val currentBlock
        get() = blocks[currentBlockIx]

    val maxHeight = 6

    /**
     * Selects the next block randomly from possible candidates.
     */
    protected fun selectNextBlock(gameState: GameState) {
        // set selected block as current
        if (currentBlockIx >= 0) {
            currentBlockY += currentBlock.endHeight
        }

        // select next block
        val candidates = ArrayList<Int>()
        for ((ix, block) in blocks.withIndex()) {
            // Check if we have space to go low enough and also not exceeding max height
            if (currentBlockY >= block.startHeight && currentBlockY + block.heightDiff < maxHeight)
                candidates.add(ix)
        }
        currentBlockIx = candidates.randomElement(gameState.game.random)
        currentBlockY -= currentBlock.startHeight
        currentBlockOffset = 0
    }

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (currentBlockIx < 0 || currentBlockOffset >= blocks[currentBlockIx].width) {
            selectNextBlock(gameState)
        }

        val col = arrayList<GameObject?>(HeightBlocks) { null }

        for (y in 0 until currentBlockY) {
            col[y] = SolidBlock()
        }
        for (y in currentBlockY until currentBlockY + currentBlock.height) {
            col[y] = currentBlock.definition[currentBlockOffset, y - currentBlockY]?.makeCopy()
        }
        for (y in currentBlockY + currentBlock.height until HeightBlocks) {
            col[y] = currentBlock.definition[currentBlockOffset, currentBlock.height - 1]?.makeCopy()
        }
        ++currentBlockOffset
        return col
    }

    override fun init(gameState: GameState) {
        currentBlockIx = -1
        currentBlockOffset = 0
        currentBlockY = 0
    }
}
