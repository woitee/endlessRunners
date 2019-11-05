package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject
import java.io.Serializable

/**
 * A GameEffect is something that can happen in a GameState without needing an argument.
 */

abstract class GameEffect : Serializable {
    /** The target of this effect */
    enum class Target { PLAYER, NONE }
    open val target: Target = Target.NONE

    /** The timing of this effect */
    enum class Timing { PERSISTENT, ONCE }
    open val timing: Timing = Timing.PERSISTENT

    /** The relativity of this effect */
    enum class Relativity { RELATIVE, ABSOLUTE }

    /** An opposite effect, if it exists */
    open val oppositeEffect: GameEffect
        get() = NoEffect
    val hasOpposite: Boolean
        get() = oppositeEffect != NoEffect

    /** Applies this effect in a GameState */
    abstract fun applyOn(gameState: GameState)
    fun findTarget(gameState: GameState): GameObject? {
        if (target == Target.PLAYER)
            return gameState.player
        return null
    }
}
