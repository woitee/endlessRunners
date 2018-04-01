package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameButtonAction
import cz.woitee.game.actions.abstract.HoldButtonAction
import cz.woitee.utils.resizeTo
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

        override fun keyPressed(e: KeyEvent?) {
            if (e != null) {
                pressedKeys.add(e.keyCode)
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            if (e != null) {
                pressedKeys.remove(e.keyCode)
            }
        }

        fun pressedAnyOf(keys: IntArray): Boolean {
            return keys.any { pressedKeys.contains(it) }
        }
    }
    val keyboardHelper = KeyboardHelper()
    var inited = false
    val keyMapping = ArrayList<IntArray>()

    val keyDefaults = arrayOf(
        intArrayOf(KeyEvent.VK_UP, KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_SPACE),
        intArrayOf(KeyEvent.VK_DOWN, KeyEvent.VK_X, KeyEvent.VK_CONTROL),
        intArrayOf(KeyEvent.VK_RIGHT, KeyEvent.VK_C, KeyEvent.VK_SHIFT),
        intArrayOf(KeyEvent.VK_LEFT, KeyEvent.VK_V, KeyEvent.VK_ALT)
    )

    override fun init(gameState: GameState) {
        if (gameState.game.visualizer == null) {
            throw InstantiationException("Keyboard Controller can't be set up on games without visualization")
        }
        gameState.game.visualizer.addKeyListener(keyboardHelper)

        if (gameState.buttons.size > keyDefaults.size) {
            println("Warning! KeyboardController will only be able to control first ${keyDefaults.size} of the ${keyMapping.size} of game actions.")
        }
        keyMapping.addAll(keyDefaults)
        inited = true
    }

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        for (i in 0 .. gameState.buttons.lastIndex) {
            val button = gameState.buttons[i]
            val keyPressed = keyboardHelper.pressedAnyOf(keyMapping[i])

            if (!button.isPressed && keyPressed) {
                return button.hold
            } else if (button.isPressed && !keyPressed)
                return button.release
        }
        return null
    }
}