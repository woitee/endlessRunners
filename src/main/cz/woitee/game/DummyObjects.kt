package cz.woitee.game

import cz.woitee.game.levelGenerators.FlatLevelGenerator
import cz.woitee.game.playerControllers.RandomPlayerController

object DummyObjects {
    fun createDummyGame(): Game {
        return Game(FlatLevelGenerator(), RandomPlayerController(), null)
    }

    fun createDummyGameState(): GameState {
        return GameState(createDummyGame(), null)
    }
}