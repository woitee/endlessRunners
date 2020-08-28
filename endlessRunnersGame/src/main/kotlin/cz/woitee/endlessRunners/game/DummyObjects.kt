package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.ButtonModel
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.RandomPlayerController
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * Dummy, simple objects, for use in testing.
 */
object DummyObjects {
    class MockAction : GameAction() {
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
            return object : IUndo {
                override fun undo(gameState: GameState) {
                    ++timesUndone
                }
            }
        }
    }
    class MockHoldAction : HoldButtonAction() {
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
    class MockGameDescription : GameDescription() {
        override val allActions: ArrayList<GameAction> = arrayListOf(
            MockAction(),
            MockHoldAction()
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
            GameState(game, null),
            GameState(game, null),
            game.updateTime
        )
    }
}
