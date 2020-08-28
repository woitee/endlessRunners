package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.ChangeShapeHoldAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.SpeedChange
import cz.woitee.endlessRunners.game.effects.TimedEffect

/**
 * An interesting game for testing the speed change actions, the player can jump, crouch, and speed up temporarily.
 */
class DashingGameDescription : GameDescription() {
    class DashingAction(amount: Double, val time: Double) : ApplyGameEffectAction(
        TimedEffect(
            time,
            SpeedChange(GameEffect.Target.PLAYER, amount),
            SpeedChange(GameEffect.Target.PLAYER, GameDescription().playerStartingSpeed)
        )
    )

    override val allActions: ArrayList<GameAction> = arrayListOf(
        JumpAction(50.0),
        ChangeShapeHoldAction(1, 1),
        DashingAction(2 * playerStartingSpeed, 0.5)
//            DashingAction(6.0, 0.5)
    )
}
