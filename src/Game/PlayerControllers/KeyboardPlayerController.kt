package Game.PlayerControllers

/**
 * Created by woitee on 04/03/2017.
 */

import Game.GameActions.*
import Game.GameState
import Game.Game
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

class KeyboardPlayerController: PlayerController() {
    /**
     * Keyboard helper that detects all keys pressed, and even those that were pressed too shortly for us to notice.
     */
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
    val keyMapping = HashMap<IGameAction, IntArray>()
    val heldActions = HashMap<IHoldAction, Boolean>()

    fun init(game: Game) {
        if (game.visualizer == null) {
            throw InstantiationException("Keyboard Controller can't be set up on games without visualization")
        }
        game.visualizer.addKeyListener(keyboardHelper)

        val allActions = game.gameDescription.allActions
        for (action in allActions) {
            if (action is JumpAction) {
                keyMapping.put(action, intArrayOf(KeyEvent.VK_UP, KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_SPACE))
            } else {
                keyMapping.put(action, intArrayOf(KeyEvent.VK_DOWN, KeyEvent.VK_X, KeyEvent.VK_CONTROL))
            }
            if (action is IHoldAction) {
                heldActions[action] = false
            }
        }
        inited = true

    }

    override fun onUpdate(gameState: GameState): PlayerControllerOutput? {
        if (!inited) {
            init(gameState.game)
        }
        val pressedKeys = keyboardHelper.getKeys()
        // Process trigger actions first, as they are impulses which may not be held until next tick
        for (action in gameState.getPerformableActions().filter{ it !is IHoldAction }) {
            val actionKeys = keyMapping[action] ?: continue
            for (actionKey in actionKeys) {
                if (actionKey in pressedKeys) {
                    return action.press()
                }
            }
        }
        // Process hold actions second, as they can be resolved one tick later
        for (action in gameState.getPerformableActions().filter{ it is IHoldAction }) {
            val actionKeys = keyMapping[action] ?: continue
            var actionKeyPressed = false
            
            // any action key pressed -> start hold action
            for (actionKey in actionKeys) {
                if (actionKey in pressedKeys) {
                    if (!heldActions[action]!!) {
                        println("Holding")
                        heldActions[action as IHoldAction] = true
                        return action.press()
                    }
                    actionKeyPressed = true
                }
            }
            // no action key pressed -> stop hold action
            if (!actionKeyPressed && heldActions[action]!!) {
                println("Releasing")
                heldActions[action as IHoldAction] = false
                return action.release()
            }
        }
        return null
    }
}