package cz.woitee.game.levelGenerators

import cz.woitee.game.GameButton
import cz.woitee.game.objects.GameObject
import cz.woitee.game.GameState
import cz.woitee.game.HeightBlocks
import cz.woitee.utils.arrayList
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

abstract class LevelGenerator{
    abstract fun generateNextColumn(gameState: GameState): ArrayList<GameObject?>
    abstract fun init(gameState: GameState)
    /**
     * Level Generators get notified about game updates - they may or may not use this information.
     */
    open fun onUpdate(updateTime: Double, appliedAction: GameButton.StateChange?, gameState: GameState) {}

    fun generateEmptyColumn(): ArrayList<GameObject?> {
        return arrayList(HeightBlocks, { null })
    }
}