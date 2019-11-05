package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import org.junit.jupiter.api.Assertions.*

internal class BlockValidatorTest {
    @org.junit.jupiter.api.Test
    fun basicGoForward() {
        runTest(arrayListOf(
                "P  P",
                "P  P",
                "####"
        ), true)
    }

    @org.junit.jupiter.api.Test
    fun impossible() {
        runTest(arrayListOf(
                "####",
                "P #P",
                "P #P",
                "####"
        ), false)
    }

    @org.junit.jupiter.api.Test
    fun stepUp() {
        runTest(arrayListOf(
                "        P",
                "P       P",
                "P   #####",
                "#########"
        ), true)
    }

    @org.junit.jupiter.api.Test
    fun stepDown() {
        runTest(arrayListOf(
                "P       ",
                "P      P",
                "####   P",
                "########"
        ), true)
    }

    @org.junit.jupiter.api.Test
    fun largeStepDown() {
        runTest(arrayListOf(
                "P          ",
                "P          ",
                "####       ",
                "####      P",
                "####      P",
                "###########"
        ), true)
    }

    @org.junit.jupiter.api.Test
    fun crouchObstacle() {
        runTest(arrayListOf(
                "   ###   ",
                "   ###   ",
                "P  ###  P",
                "P       P",
                "#########"
        ), true)
    }

    @org.junit.jupiter.api.Test
    fun comboDownUp() {
        runTest(arrayListOf(
                "   ###        ",
                "   ###        ",
                "P  ###       P",
                "P            P",
                "#        #####",
                "##############"
        ), true)
    }

    fun runTest(stringBlock: ArrayList<String>, expectedResult: Boolean, gameDescription: GameDescription = CrouchGameDescription()) {
        val blockValidator = BlockValidator(gameDescription, BasicDFS())
        val result = blockValidator.validate(HeightBlock(gameDescription, stringBlock))

        assertEquals(expectedResult, result)
    }
}
