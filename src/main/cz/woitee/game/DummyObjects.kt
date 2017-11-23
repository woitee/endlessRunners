package cz.woitee.game

import cz.woitee.game.actions.ChangeColorAction
import cz.woitee.game.actions.ChangeShapeAction
import cz.woitee.game.actions.JumpAction
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.descriptions.GameDescription
import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.game.objects.GameObjectColor
import cz.woitee.game.playerControllers.RandomPlayerController
import cz.woitee.game.undoing.IUndo

object DummyObjects {
    class MockAction: GameAction() {
        var timesApplied = 0
        var timesUndone = 0

        override fun isApplicableOn(gameState: GameState): Boolean {
            return true
        }

        override fun applyOn(gameState: GameState) {
            ++timesApplied
        }

        override fun applyUndoablyOn(gameState: GameState): IUndo {
            ++timesApplied
            return object: IUndo {
                override fun undo(gameState: GameState) {
                    ++timesUndone
                }
            }
        }
    }
    class MockGameDescription: GameDescription() {
        override val allActions: List<GameAction> = listOf(
            MockAction()
        )
    }

    fun createDummyGame(): Game {
        return Game(FlatLevelGenerator(), RandomPlayerController(), null, gameDescription = MockGameDescription())
    }

    fun createDummyGameState(): GameState {
        return GameState(createDummyGame(), null)
    }
}