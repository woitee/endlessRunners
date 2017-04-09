package Game.PlayerControllers

/**
 * Created by woitee on 04/03/2017.
 */

import Game.GameActions.IGameAction
import Game.GameActions.JumpAction
import Game.GameState
import Game.Game
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

class KeyboardPlayerController: PlayerController() {
    class KeyboardHelper: KeyAdapter() {
        var pressedKeys = HashSet<Int>()
        var pressedSinceLastUpdate = HashSet<Int>()

        override fun keyPressed(e: KeyEvent?) {
            if (e != null) {
                pressedKeys.add(e.keyCode)
                pressedSinceLastUpdate.add(e.keyCode)
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            if (e != null) {
                pressedKeys.remove(e.keyCode)
            }
        }

        public fun getKeys(): Set<Int> {
            val res = pressedSinceLastUpdate
            res.addAll(pressedKeys)
            pressedSinceLastUpdate = HashSet<Int>()
            return res
        }
    }
    val keyboardHelper = KeyboardHelper()

    var inited = false
    fun init(game: Game) {
        if (game.visualizer == null) {
            throw InstantiationException("Keyboard Controller can't be set up on games without visualization")
        }
        game.visualizer.addKeyListener(keyboardHelper)
    }

    override fun onUpdate(gameState: GameState): IGameAction? {
        if (!inited) {
            init(gameState.game)
            inited = true
        }
        val keys = keyboardHelper.getKeys()
//        if (KeyEvent.VK_UP in keys) {
        if (keys.count() > 0) {
            return gameState.getPerformableActions().get(0)
        }
        return null
    }
}