package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import org.junit.jupiter.api.Assertions.*

class EvoBlockRunnerTest {
    @org.junit.jupiter.api.Test
    fun testSimpleBlock() {
        val blocks = ArrayList<HeightBlock>()
        repeat(3) {
            val runner = EvoBlockRunner(CrouchGameDescription(), { DFSPlayerController() }, seed = 1024L)
            blocks.add(runner.evolveBlock())
        }

        for (i in 1 until blocks.count()) {
            assertEquals(blocks[0].toString(), blocks[i].toString(), "Blocks 0 and $i are not the same!")
        }
    }
}
