package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject

/**
 * Created by woitee on 16/01/2017.
 */

abstract class GameEffect {
    enum class Target { PLAYER, NONE }
    open val target: Target = Target.NONE

    enum class Timing { PERSISTENT, ONCE }
    open val timing: Timing = Timing.PERSISTENT

    abstract fun applyOn(gameState: GameState)
    fun findTarget(gameState: GameState): GameObject? {
        if (target == Target.PLAYER)
            return gameState.player
        return null
    }
}