package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.geom.Vector2Int
import nl.pvdberg.hashkode.compareFields
import nl.pvdberg.hashkode.hashKode

/**
 * A HeightBlock - representation of a chunk of the game, where the Player can start at any height on the left,
 * and end on the right. An element mainly for HeightBlockLevelGenerator.
 */
class HeightBlock : Block {
    var difficulty: Int = 0
    var startHeight: Int = 0
    var endHeight: Int = 0
    val heightDiff: Int
        get() = endHeight - startHeight

    val goesUp: Boolean
        get() = endHeight > startHeight
    val goesDown: Boolean
        get() = endHeight < startHeight

    constructor(width: Int, height: Int) : super(width, height)
    constructor(gameDescription: GameDescription, stringBlock: List<String>) : super(gameDescription, stringBlock) {
        startHeight = -1
        endHeight = -1
        val maxX = width - 1
        for (y in 0 until height) {
            if (definition[0, y]?.gameObjectClass == GameObjectClass.PLAYER) {
                definition[0, y] = null
                if (startHeight == -1) startHeight = y - 1
            }
            if (definition[maxX, y]?.gameObjectClass == GameObjectClass.PLAYER) {
                definition[maxX, y] = null
                if (endHeight == -1) endHeight = y - 1
            }
        }
    }

    override fun toString(): String {
        val str = super.toString()
        val playerCharLocations = arrayListOf(
            Vector2Int(0, startHeight + 1),
            Vector2Int(0, startHeight + 2),
            Vector2Int(width - 1, endHeight + 1),
            Vector2Int(width - 1, endHeight + 2)
        )
        val chars = str.toCharArray()
        for (location in playerCharLocations) {
            val abs = ((height - 1 - location.y) * (width + 1)) + location.x
            chars[abs] = 'P'
        }
        return String(chars)
    }

    override fun equals(other: Any?) = compareFields(other) {
        equal = one.startHeight == two.startHeight
                && super.equals(other)
    }

    override fun hashCode() = hashKode(startHeight, endHeight, super.hashCode())
}
