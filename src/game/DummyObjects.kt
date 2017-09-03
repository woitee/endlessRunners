package game

import game.pcg.FlatLevelGenerator
import game.playerControllers.RandomPlayerController

object DummyObjects {
    fun createDummyGame(): Game {
        return Game(FlatLevelGenerator(), RandomPlayerController(), null)
    }

    fun createDummyGameState(): GameState {
        return GameState(createDummyGame(), null)
    }
}