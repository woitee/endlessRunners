package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import org.junit.jupiter.api.Assertions.*

class EvoBlockMethodsTest {
    @org.junit.jupiter.api.Test
    fun testBlockPostprocessing() {
        val gameDescription = CrouchGameDescription()
        val evoMethods = EvoBlockMethods(gameDescription, { DFSPlayerController() })

        val block = HeightBlock(
            gameDescription,
            arrayListOf(
                "P  P",
                "P  P",
                "####",
                "####",
                "#  #"
            )
        )

        assertEquals(2, block.startHeight)
        assertEquals(2, block.endHeight)
        assertEquals('#', block.definition[0, 0]?.dumpChar)
        assertEquals('#', block.definition[0, 1]?.dumpChar)

        evoMethods.postprocessHeightBlock(block)
        println("BEGIN BLOCK")
        print(block)
        println("END BLOCK")

        assertEquals(0, block.startHeight)
        assertEquals(0, block.endHeight)
        assertEquals('#', block.definition[0, 0]?.dumpChar)
        assertEquals(null, block.definition[0, 1]?.dumpChar)
    }

    @org.junit.jupiter.api.Test
    fun testBlockEncoding() {
        val gameDescription = BitTriGameDescription()
        val evoMethods = EvoBlockMethods(
            gameDescription,
            { DFSPlayerController() },
            5,
            5
        )

        val block1 = HeightBlock(
            gameDescription,
            arrayListOf(
                "    P",
                "0   P",
                "P   #",
                "P123#",
                "#####"
            )
        )

        val block2 = HeightBlock(
            gameDescription,
            arrayListOf(
                "P    ",
                "P    ",
                "#   P",
                "#123P",
                "#####"
            )
        )

        for (block in arrayOf(block1, block2)) {
            val genotype = evoMethods.block2genotype(block)

            val newBlock = evoMethods.genotype2block(genotype)
            assertEquals(block, newBlock)
        }
    }

    @org.junit.jupiter.api.Test
    fun testBlockPadding() {
        val gameDescription = BitTriGameDescription()
        val evoMethods = EvoBlockMethods(
            gameDescription,
            { DFSPlayerController() },
            7,
            7
        )

        val block = HeightBlock(
            gameDescription,
            arrayListOf(
                "P  P",
                "P  P",
                "####"
            )
        )

        val paddedBlock = HeightBlock(
            gameDescription,
            arrayListOf(
                "       ",
                "P     P",
                "P     P",
                "#######"
            )
        )

        assertEquals(
            paddedBlock,
            evoMethods.padBlock(block, paddedBlock.dimensions)
        )
    }
}
