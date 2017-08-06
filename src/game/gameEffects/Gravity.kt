package game.gameEffects

import game.gameObjects.MovingObject
import game.GameState
import game.undoing.IUndo
import game.undoing.NoActionUndo

/**
 * Gravity effect, defaultly targeted on player, but can also be used for other objects.
 * It also states that the objects collide between each other.
 *
 * Created by woitee on 16/01/2017.
 */

class Gravity(
        targetedAt: GameEffect.Target = GameEffect.Target.PLAYER,
        var strength: Double = 0.5): UndoableGameEffect() {

    class GravityUndo(val gravity: Gravity): IUndo {
        override fun undo(gameState: GameState) {
            val target = (gravity.findTarget(gameState) as MovingObject?) ?: return
            target.yspeed += gravity.strength * game.BlockHeight * gameState.game.updateTime
        }
    }

    override val target = targetedAt
    override val timing = GameEffect.Timing.PERSISTENT

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return
        if (gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid ?: false) {
            return
        }

        target.yspeed -= strength * game.BlockHeight * gameState.game.updateTime
    }

//    var statApplied = 0
//    var statTotal = 0
//    var printStatEvery = 75
    override fun applyUndoablyOn(gameState: GameState): IUndo {
//        statTotal++
        val target = (findTarget(gameState) as MovingObject?) ?: return NoActionUndo
        if (gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid ?: false) {
            return NoActionUndo
        }
        target.yspeed -= strength * game.BlockHeight * gameState.game.updateTime
//        statApplied += 1
//        if (statApplied >= printStatEvery - 1) {
//            println("$statApplied from $statTotal")
//            statApplied = 0
//            statTotal = 0
//        }
        return GravityUndo(this)
    }
}