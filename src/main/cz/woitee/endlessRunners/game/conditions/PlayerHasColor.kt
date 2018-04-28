package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObjectColor

class PlayerHasColor(val color: GameObjectColor): GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return gameState.player.color == color
    }
}