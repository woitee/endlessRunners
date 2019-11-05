package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import org.junit.jupiter.api.Assertions.*

class EvoBlockMethodsTest {
    @org.junit.jupiter.api.Test
    fun testBlockPostprocessing() {
        val gameDescription = CrouchGameDescription()
        val evoMethods = EvoBlockMethods(gameDescription, { DFSPlayerController() })

        val block = HeightBlock(gameDescription, arrayListOf(
                "P  P",
                "P  P",
                "####",
                "####",
                "#  #"
        ))

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
}
