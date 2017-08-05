package game.pcg

import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.GameState
import game.HeightBlocks
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

        if (gridX in 5..9) {
            col[3] = SolidBlock()
        } else if (gridX in 15..19) {
            col[2] = SolidBlock()
        } else if (gridX > 50) {
            col[2] = SolidBlock()
            col[3] = SolidBlock()
            col[4] = SolidBlock()
            col[5] = SolidBlock()
        }
        return col
    }

    override fun reset() {

    }

}