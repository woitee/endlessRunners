package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.utils.arrayList

/**
 * Simple utility class for generating content.
 */
object LevelGenerationUtils {
    /**
     * Generates a column based on a string representation (# represent solid block, numbers custom objects, spaces air).
     *
     * @param str The string to generate from
     * @param gameState The gameState we generate for
     */
    fun generateColumnFromString(str: String, gameState: GameState): ArrayList<GameObject?> {
        val res: ArrayList<GameObject?> = arrayList(gameState.grid.height, { null })
        assert(str.length <= res.count())
        for (i in 0 until str.length) {
            res[i] = gameState.game.gameDescription.charToObject[str[i]]?.makeCopy()
        }
        return res
    }
}
