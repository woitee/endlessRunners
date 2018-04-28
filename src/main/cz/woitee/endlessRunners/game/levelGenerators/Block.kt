package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.Grid2D
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.objects.GameObject
import java.security.InvalidParameterException

class Block (val width: Int, val height: Int) {
    val definition = Grid2D<GameObject?>(width, height, { null })

    constructor (gameDescription: GameDescription, stringBlock: List<String>): this(stringBlock[0].length, stringBlock.count()) {
        for (y in stringBlock.lastIndex.downTo(0)) {
            for (x in 0 .. width - 1) {
                if (stringBlock[y][x] == 'P') {
                    throw InvalidParameterException("Player should not be part of a generator Block!")
                }
                definition[x, height - y - 1] = gameDescription.charToObject[stringBlock[y][x]]
            }
        }
    }
}