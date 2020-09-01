package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.Grid2D
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.geom.Vector2Int
import nl.pvdberg.hashkode.compareFields
import java.io.Serializable

/**
 * A block, that is always at ground level.
 */
open class Block(val width: Int, val height: Int) : Serializable {
    val definition = Grid2D<GameObject?>(width, height) { null }
    val dimensions
        get() = Vector2Int(width, height)

    constructor (gameDescription: GameDescription, stringBlock: List<String>) : this(stringBlock[0].length, stringBlock.count()) {
        for (y in stringBlock.lastIndex.downTo(0)) {
            for (x in 0 until width) {
                definition[x, height - y - 1] = gameDescription.charToObject[stringBlock[y][x]]
            }
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (y in (height - 1).downTo(0)) {
            for (x in 0 until width) {
                stringBuilder.append(definition[x, y]?.dumpChar ?: ' ')
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    override fun equals(other: Any?) = compareFields(other) {
        equal = one.definition == two.definition
    }
}
