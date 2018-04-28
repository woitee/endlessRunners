//package cz.woitee.endlessRunners.game.playerControllers
//
//import cz.woitee.endlessRunners.game.GameState
//import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
//import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
//import cz.woitee.endlessRunners.game.undoing.IUndo
//import cz.woitee.endlessRunners.game.undoing.NoUndo
//
///**
// * Created by woitee on 23/07/2017.
// */
//data class ElementaryAction(val gameAction: GameButtonAction, val isPress: Boolean) {
//    fun applyOn(gameState: GameState) {
//        if (isPress)
//            gameAction.applyOn(gameState)
//        else if (!isPress && gameAction is HoldButtonAction)
//            gameAction.stopApplyingOn(gameState)
//    }
//    fun applyUndoablyOn(gameState: GameState): IUndo {
//        if (isPress)
//            return gameAction.applyUndoablyOn(gameState)
//        else if (!isPress && gameAction is HoldButtonAction)
//            return gameAction.stopApplyingUndoablyOn(gameState)
//
//        // in case of releasing a non-hold
//        return NoUndo
//    }
//    fun isApplicableOn(gameState: GameState): Boolean {
//        if (isPress)
//            return gameAction.isApplicableOn(gameState)
//        else if (!isPress && gameAction is HoldButtonAction)
//            return gameAction.canBeStoppedApplyingOn(gameState)
//
//        // in case of releasing a non-hold, it doesn't do anything
//        return false
//    }
//}