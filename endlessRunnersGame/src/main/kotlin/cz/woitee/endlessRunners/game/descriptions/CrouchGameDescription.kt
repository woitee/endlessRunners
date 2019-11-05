package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.actions.ChangeShapeHoldAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.Gravity

/**
 * A simple game with two actions - jump and crouch.
 */
open class CrouchGameDescription : GameDescription() {
    override val permanentEffects = arrayListOf<GameEffect>(Gravity(GameEffect.Target.PLAYER, 100 * 1.3 / BlockHeight))

    override val allActions: ArrayList<GameAction> = arrayListOf(
            JumpAction(25.0),
            ChangeShapeHoldAction(2, 1)
    )
}
