package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.ChangeShapeAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.SpeedChange
import cz.woitee.endlessRunners.game.effects.TimedEffect

class DashingGameDescription : GameDescription() {
    class DashingAction(amount: Double, val time: Double) : ApplyGameEffectAction(TimedEffect(
        time,
        SpeedChange(GameEffect.Target.PLAYER, amount),
        SpeedChange(GameEffect.Target.PLAYER, GameDescription().playerStartingSpeed)
    ))

    override val allActions: List<GameButtonAction> = listOf(
            JumpAction(22.0),
            ChangeShapeAction(1, 1),
            DashingAction(2 * playerStartingSpeed, 0.5),
            DashingAction(1.0, 0.5)
    )
}
