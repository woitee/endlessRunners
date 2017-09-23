package game.conditions

import game.GameState
import game.objects.GameObjectColor

class PlayerHasColor(val color: GameObjectColor): GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return gameState.player.color == color
    }
}