package game.levelGenerators

import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.GameState
import game.HeightBlocks
import game.gameObjects.CustomBlock
import utils.arrayList
import java.util.*

/**
 * Created by woitee on 23/07/2017.
 */

class TestLevelGenerator: ILevelGenerator {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        val gridX = gameState.gridX

        if (gridX % 10 == 0) {
            col[0] = CustomBlock(0)
        }
        return col
    }

    override fun reset() {

    }

}