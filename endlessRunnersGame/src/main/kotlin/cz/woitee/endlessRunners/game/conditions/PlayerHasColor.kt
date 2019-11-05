package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObjectColor

/**
 * A condition that is true if the player has a specific color.
 *
 * @param color The specific color.
 */
class PlayerHasColor(val color: GameObjectColor) : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return gameState.player.color == color
    }

    override fun toString(): String {
        return "PlayerHasColor($color)"
    }
}
