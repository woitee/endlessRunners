package cz.woitee.game.levelGenerators

import cz.woitee.game.GameState
import cz.woitee.game.objects.GameObject
import cz.woitee.utils.arrayList

object LevelGenerationUtils {
    fun generateColumnFromString(str: String, gameState: GameState): ArrayList<GameObject?> {
        val res: ArrayList<GameObject?> = arrayList(gameState.grid.height, { null })
        assert(str.length <= res.count())
        for (i in 0 until str.length) {
            res[i] = gameState.game.gameDescription.charToObject[str[i]]?.makeCopy()
        }
        return res
    }
}