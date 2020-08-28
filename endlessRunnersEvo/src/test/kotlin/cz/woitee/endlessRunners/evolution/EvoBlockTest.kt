package cz.woitee.endlessRunners.evolution

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockUtils
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import org.junit.jupiter.api.Assertions.*

internal class EvoBlockTest {
    @org.junit.jupiter.api.Test
    fun testSimpleBlock() {
        doBasicTest(
            arrayListOf(
                "   ",
                "###",
                "   "
            ),
            1
        )
    }

    @org.junit.jupiter.api.Test
    fun testAdvancedBlock() {
        doBasicTest(
            arrayListOf(
                " # ",
                "###",
                " # "
            ),
            3
        )
    }

    @org.junit.jupiter.api.Test
    fun testMoreAdvancedBlock() {
        doBasicTest(
            arrayListOf(
                " #  ",
                " ###",
                "### ",
                "  # "
            ),
            4
        )
    }

    private fun doBasicTest(stringBlock: ArrayList<String>, smoothness: Int) {
        val gameDescription = CrouchGameDescription()

        val block1 = HeightBlock(gameDescription, stringBlock)

        assertEquals(
            smoothness,
            EvoBlockUtils.calculateRuggedness(block1)
        )
    }
}
