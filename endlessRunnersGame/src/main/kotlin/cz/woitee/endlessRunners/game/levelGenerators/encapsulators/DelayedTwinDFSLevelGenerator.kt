package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import java.util.ArrayList

/**
 * A specially modified instance using DelayedTwinDFS to validate results of inner LevelGenerator. It keeps the inner
 * states between frames (it has to). To accomplish this, it monitors what the player does in between states.
 */
class DelayedTwinDFSLevelGenerator(delayTime: Double, innerGenerator: LevelGenerator, doDFSAfterFail: Boolean = false, dumpErrors: Boolean = true) :
    DFSEnsuring(innerGenerator, DelayedTwinDFS(delayTime, allowSearchInBeginning = true), doDFSAfterFail, dumpErrors) {

    val delayedTwin: DelayedTwinDFS
        get() = dfsProvider as DelayedTwinDFS

    override fun onUpdate(updateTime: Double, appliedAction: GameButton.StateChange?, gameState: GameState) {
        delayedTwin.onUpdate(updateTime, appliedAction, gameState)
    }

    override fun beforeDFS(gameState: GameState) {
        // dont reset the dfsProvider, parent does it, but we want to keep internal state between frames
    }

    override fun generateBackupColumn(gameState: GameState): ArrayList<GameObject?> {
        // We need to also reset to this column in our private GameStates

        val backupColumn = super.generateBackupColumn(gameState)
        delayedTwin.buttonModel.undoAddColumn(generateEmptyColumn())
        delayedTwin.buttonModel.addColumn(backupColumn)
        delayedTwin.statesCache.clearAddedSince(gameState.gameTime)

        return backupColumn
    }
}
