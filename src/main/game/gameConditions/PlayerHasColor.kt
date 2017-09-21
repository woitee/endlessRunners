package game.gameConditions

import game.GameState
import game.gameObjects.GameObjectColor

class PlayerHasColor(val color: GameObjectColor): GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return gameState.player.color == color
    }
}