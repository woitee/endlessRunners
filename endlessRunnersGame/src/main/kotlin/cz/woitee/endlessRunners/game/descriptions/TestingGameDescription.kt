package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.NoAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalAction
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.Gravity
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectColor

/**
 * A game description for reproducing behaviors.
 */
class TestingGameDescription : GameDescription() {
    override val customObjects: ArrayList<GameObject> = arrayListOf(CustomBlock(0), CustomBlock(1))
    override val allActions: ArrayList<GameAction> = arrayListOf(
        ConditionalAction(PlayerHasColor(GameObjectColor.YELLOW), NoAction, NoAction),
        JumpAction(24.79751808363527),
        ApplyGameEffectAction(Gravity(GameEffect.Target.PLAYER, -0.7487525106783748)),
        ConditionalAction(PlayerHasColor(GameObjectColor.YELLOW), NoAction, NoAction)
    )
    override val permanentEffects: ArrayList<GameEffect> = arrayListOf(
        Gravity(GameEffect.Target.PLAYER, 0.55004845852142)
    )
}
