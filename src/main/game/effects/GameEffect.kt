package game.effects

import game.GameState
import game.objects.GameObject
import game.undoing.IApplicable

/**
 * Created by woitee on 16/01/2017.
 */

abstract class GameEffect: IApplicable {
    enum class Target { PLAYER, NONE }
    open val target: Target = Target.NONE

    enum class Timing { PERSISTENT, ONCE }
    open val timing: Timing = Timing.PERSISTENT

    abstract override fun applyOn(gameState: GameState)
    fun findTarget(gameState: GameState): GameObject? {
        if (target == Target.PLAYER)
            return gameState.player
        return null
    }
}