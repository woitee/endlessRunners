package cz.woitee.game.actions.abstract

/** Util class for casting hold actions into two GameActions, which also provides references on the other from the pair.
 */
abstract class HalfOfHoldAction: GameAction() {
    // This is merely a GameAction, because we want to override it to an UndoableAction further
    abstract val opposite: HalfOfHoldAction
}