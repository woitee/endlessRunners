package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.WidthBlocks
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.utils.arrayList
import java.util.*

/**
 * A generator that lets the generator create one screen-width of content, and then creates an impassable wall.
 *
 * Useful as Kobayashi Maru to test PlayerController behaviour when facing a certain loss.
 * DFS for example can have problems with performance.
 */
class OneScreenThenDeath(val innerGenerator: LevelGenerator) : LevelGenerator() {
    var numCalled = 0

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (numCalled <= WidthBlocks) {
            numCalled++
            return innerGenerator.generateNextColumn(gameState)
        }
        return arrayList(HeightBlocks, { SolidBlock() })
    }

    override fun init(gameState: GameState) {
        innerGenerator.init(gameState)
    }
}
