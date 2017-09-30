package cz.woitee.game.conditions

import cz.woitee.game.GameState
import cz.woitee.game.objects.GameObjectColor

class PlayerHasColor(val color: GameObjectColor): GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return gameState.player.color == color
    }
}