package cz.woitee.endlessRunners.game.effects

/**
 * An effect, that is Timed for categorization purposes, but does nothing.
 */
object NoTimedEffect : TimedEffect(0.0, NoEffect, NoEffect, NoEffect) {
    override fun toString(): String {
        return "NoTimedEffect"
    }
}
