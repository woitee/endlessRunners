package Game.GameActions

import Game.GameState
import Game.BlockHeight
import Game.BlockWidth
import Game.GameActions.IGameAction
import Game.GameObjects.SolidBlock

/**
 * Created by woitee on 13/01/2017.
 */

class JumpAction: IGameAction {
    override fun isPerformableOn(gameState: GameState): Boolean {
        val x = gameState.player.x
        val y = gameState.player.y - 1
        val gridX = (x / BlockWidth).toInt() - gameState.gridX
        val gridY = (y / BlockHeight).toInt()

        return gameState.grid[gridX, gridY] is SolidBlock
    }

    override fun performOn(gameState: GameState) {
        gameState.player.yspeed = 0.1
    }
}