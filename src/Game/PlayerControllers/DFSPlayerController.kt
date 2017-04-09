package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameActions.IUndoableAction
import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.MultiUndo
import Utils.pop
import java.util.*

/**
 * Created by woitee on 09/04/2017.
 */
class DFSPlayerController: PlayerController() {
    val maxDepth = 10
    var updateTime = 50L
    var readyToDie = false

    override fun onUpdate(gameState: GameState): IGameAction? {
        if (updateTime < 0)
            updateTime = gameState.game.updateTime

//        println("Player pos ${gameState.player.positionOnScreen()}")

        if (readyToDie)
            return null

        return performDFS(gameState)
    }

    override fun reset() {
        super.reset()
        readyToDie = false
    }

    fun performDFS(gameState: GameState): IGameAction? {
        synchronized(gameState.gameObjects) {
            var depth = 0
            val undoList = ArrayList<IUndo>()
            val actionList = ArrayList<Int>()

            while (depth < maxDepth && gameState.player.positionOnScreen() < 600) {
                depth++
                undoList.add(advanceState(gameState, null))
                actionList.add(-1)
                if (gameState.isGameOver) {
                    //backtrack
                    var finishedBacktrack = false
                    while (!finishedBacktrack) {
                        if (undoList.isEmpty()) {
                            // prepared to die
                            readyToDie = true
                            println("I don't fear death!")
                            return null
                        }
                        depth--
                        undoList.pop().undo(gameState)
                        val action = actionList.pop() + 1
                        val actions = gameState.getPerformableActions()
                        if (action < actions.count()) {
                            undoList.add(advanceState(gameState, actions[action]))
                            actionList.add(action)
                            finishedBacktrack = true
                        }
                    }
                }
            }
            for (undo in undoList.asReversed())
                undo.undo(gameState)

            val action = actionList[0]
            return if (action == -1) null else gameState.getPerformableActions()[action]
        }
    }

    private fun advanceState(gameState: GameState, gameAction: IGameAction?): IUndo {
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else
            return MultiUndo(listOf(
                    gameState.advanceUndoable(updateTime),
                    (gameAction as IUndoableAction).applyUndoableOn(gameState)
            ))
    }
}