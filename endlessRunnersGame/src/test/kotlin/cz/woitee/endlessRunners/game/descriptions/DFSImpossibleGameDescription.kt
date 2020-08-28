package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.SpeedChange

class DFSImpossibleGameDescription : GameDescription() {
    override var playerStartingSpeed = 1.0

    override val allActions = arrayListOf<GameAction>(
        ApplyGameEffectAction(SpeedChange(GameEffect.Target.PLAYER, 0.01, GameEffect.Relativity.RELATIVE))
    )
}
