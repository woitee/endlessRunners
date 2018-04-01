package cz.woitee.game

import cz.woitee.game.actions.abstract.GameButtonAction
import cz.woitee.game.actions.abstract.HoldButtonAction
import cz.woitee.game.algorithms.dfs.delayedTwin.ButtonModel
import cz.woitee.game.descriptions.GameDescription
import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.game.playerControllers.RandomPlayerController
import cz.woitee.game.undoing.IUndo

object DummyObjects {
    class MockAction: GameButtonAction() {
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
    class MockHoldButtonAction : HoldButtonAction() {
        var isApplicableCalled = 0
        var keptApplyingCalled = 0
        var canBeStoppedApplyingCalled = 0
        var applyCalled = 0
        var stopApplyingCalled = 0
        var timesUndone = 0
        var timesStopUndone = 0

        override fun isApplicableOn(gameState: GameState): Boolean {
            ++isApplicableCalled
            return true
        }

        override fun canBeKeptApplyingOn(gameState: GameState): Boolean {
            ++keptApplyingCalled
            return true
        }

        override fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
            ++canBeStoppedApplyingCalled
            return true
        }

        override fun applyOn(gameState: GameState) {
            ++applyCalled
        }

        override fun stopApplyingOn(gameState: GameState) {
            ++stopApplyingCalled
        }

        override fun applyUndoablyOn(gameState: GameState): IUndo {
            applyOn(gameState)
            return object : IUndo {
                override fun undo(gameState: GameState) {
                    ++timesUndone
                }
            }
        }

        override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
            stopApplyingOn(gameState)
            return object : IUndo {
                override fun undo(gameState: GameState) {
                    ++timesStopUndone
                }
            }
        }
    }
    class MockGameDescription: GameDescription() {
        override val allActions: List<GameButtonAction> = listOf(
            MockAction(), MockHoldButtonAction()
        )
    }

    fun createDummyGame(): Game {
        return Game(FlatLevelGenerator(), RandomPlayerController(), null, gameDescription = MockGameDescription())
    }

    fun createDummyGameState(): GameState {
        return GameState(createDummyGame(), null)
    }

    fun createDummyButtonModel(): ButtonModel {
        val game = createDummyGame()
        return ButtonModel(
                GameState(game, null), GameState(game, null), game.updateTime
        )
    }
}