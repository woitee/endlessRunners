//package cz.woitee.game.algorithms.dfs.delayedTwin
//
//import cz.woitee.game.GameState
//import cz.woitee.game.actions.abstract.HoldAction
//import cz.woitee.game.actions.abstract.UndoableAction
//import cz.woitee.game.actions.abstract.UndoableHalfOfHoldAction
//import cz.woitee.game.actions.abstract.UndoableHoldAction
//import cz.woitee.game.undoing.IUndo
//import java.util.*
//
///**
// * A utility class which handles button pressing for DelayedTwinDFS (caching actions caused by already pressed buttons,
// * etc.).
// */
//class ButtonModel(var currentState: GameState, var delayedState: GameState) {
//    abstract class ButtonModelAction {
//        abstract fun opposite(): ButtonModelAction
//        abstract fun isApplicableOn(buttonModel: ButtonModel): Boolean
//        abstract fun applyOn(buttonModel: ButtonModel)
//        abstract fun applyUndoablyOn(buttonModel: ButtonModel)
//    }
//
//    class ButtonAction(val gameAction: UndoableAction): ButtonModelAction() {
//        override fun isApplicableOn(buttonModel: ButtonModel): Boolean {
//            return gameAction.isApplicableOn(buttonModel.currentState)
//                    || gameAction.isApplicableOn(buttonModel.delayedState)
//                    || (gameAction is UndoableHalfOfHoldAction && buttonModel.queuedInEither(gameAction.opposite))
//        }
//
//        override fun applyOn(buttonModel: ButtonModel) {
//            if (gameAction.isApplicableOn(buttonModel.currentState))
//                gameAction.applyOn(buttonModel.currentState)
//
//            if (gameAction.isApplicableOn())
//        }
//
//        override fun applyUndoablyOn(buttonModel: ButtonModel): IUndo {
//        }
//    }
//
//    val queuedForCurrent = ArrayList<UndoableAction>()
//    val queuedForDelayed = ArrayList<UndoableAction>()
//
//    fun setStates(gameState: GameState, delayedState: GameState) {
//        this.currentState = gameState
//        this.delayedState = delayedState
//        queuedForCurrent.clear()
//        queuedForDelayed.clear()
//    }
//
//    fun queuedInEither(action: UndoableAction): Boolean {
//        return queuedForCurrent.contains(action) || queuedForDelayed.contains(action)
//    }
//
//    fun orderedApplicableActions(): ArrayList<UndoableAction?> {
//        val list = ArrayList<UndoableAction?>()
//
//        // stop holding non-holdable action
//        for (action in currentState.allActions) {
//            if (action !is HoldAction) {
//
//            }
//        }
//        for (heldAction in currentState.heldActions.keys) {
//            if (heldAction.canBeStoppedApplyingOn(currentState))
//                list.add((heldAction as UndoableHoldAction).asStopAction)
//        }
//        // do nothing
//        list.add(null)
//        // do a non-holding action
//        for (action in currentState.allActions) {
//            if (action !is UndoableHoldAction && action.isApplicableOn(currentState))
//                list.add(action as UndoableAction)
//
//        }
//        // start holding an action
//        for (action in currentState.allActions) {
//            if (action is UndoableHoldAction && action.isApplicableOn(currentState))
//                list.add(action.asStartAction)
//        }
//        return list
//    }
//
//    fun applyUndoably(action: UndoableAction?) {
//
//    }
//}