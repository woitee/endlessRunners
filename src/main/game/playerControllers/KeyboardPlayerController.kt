package game.playerControllers

import game.GameState
import game.Game
import game.actions.abstract.GameAction
import game.actions.abstract.HoldAction
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*

/**
 * Class that controls the player via keyboard.
 *
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
    val keyMapping = HashMap<GameAction, IntArray>()
    val heldActions = HashMap<HoldAction, Boolean>()

    val keyDefaults = arrayOf(
        intArrayOf(KeyEvent.VK_UP, KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_SPACE),
        intArrayOf(KeyEvent.VK_DOWN, KeyEvent.VK_X, KeyEvent.VK_CONTROL),
        intArrayOf(KeyEvent.VK_RIGHT, KeyEvent.VK_C, KeyEvent.VK_SHIFT),
        intArrayOf(KeyEvent.VK_LEFT, KeyEvent.VK_V, KeyEvent.VK_ALT)
    )

    fun init(game: Game) {
        if (game.visualizer == null) {
            throw InstantiationException("Keyboard Controller can't be set up on games without visualization")
        }
        game.visualizer.addKeyListener(keyboardHelper)

        val allActions = game.gameDescription.allActions
        if (allActions.count() > keyDefaults.count()) {
            println("Warning! KeyboardController will only be able to control first ${keyDefaults.count()} of the ${allActions.count()} of game actions.")
        }
        for (i in 0 .. allActions.lastIndex) {
            val action = allActions[i]

            keyMapping.put(action, keyDefaults[i])
            if (action is HoldAction) {
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
        for (action in gameState.getPerformableActions().filter{ it !is HoldAction }) {
            val actionKeys = keyMapping[action] ?: continue
            for (actionKey in actionKeys) {
                if (actionKey in pressedKeys) {
                    return action.press()
                }
            }
        }
        // Process hold actions second, as they can be resolved one tick later
        for (action in gameState.game.gameDescription.allActions.filter{ it is HoldAction }) {
            val actionKeys = keyMapping[action] ?: continue
            var actionKeyPressed = false

            // any action key pressed -> start hold action
            for (actionKey in actionKeys) {
                if (actionKey in pressedKeys) {
                    if (!heldActions[action]!!) {
                        heldActions[action as HoldAction] = true
                        return action.press()
                    }
                    actionKeyPressed = true
                }
            }
            // no action key pressed -> stop hold action
            if (!actionKeyPressed && heldActions[action]!! && (action as HoldAction).canBeStoppedApplyingOn(gameState)) {
                heldActions[action] = false
                return action.release()
            }
        }
        return null
    }
}