package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.objects.GameObjectClass

class HeightBlock(gameDescription: GameDescription, stringBlock: List<String>) : Block(gameDescription, stringBlock) {
    var difficulty: Int = 0
    var startHeight: Int = -1
    var endHeight: Int = -1
    val heightDiff: Int
        get() = endHeight - startHeight

    init {
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
}
