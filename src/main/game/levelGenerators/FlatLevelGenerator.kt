package game.levelGenerators

import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.GameState
import game.HeightBlocks
import java.util.*
import utils.arrayList

/**
 * Created by woitee on 15/01/2017.
 */

class FlatLevelGenerator: ILevelGenerator {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        return col
    }

    override fun reset() {
    }
}
