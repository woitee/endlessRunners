package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*

/**
 * Class that controls the player via keyboard.
 */

class KeyboardPlayerController : PlayerController() {
    /**
     * Keyboard helper that detects all keys pressed, and even those that were pressed too shortly for us to notice.
     */
    class KeyboardHelper : KeyAdapter() {
        var pressedKeys = HashSet<Int>()
        var pressedCounts = HashMap<Int, Int>()

        override fun keyPressed(e: KeyEvent?) {
            if (e != null) {
                pressedKeys.add(e.keyCode)
                pressedCounts.put(e.keyCode, pressedCounts.getOrDefault(e.keyCode, 0) + 1)
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

        fun totalPressesOf(keys: IntArray): Int {
            return keys.map { pressedCounts.getOrDefault(it, 0) }.sum()
        }

        fun init() {
            pressedCounts.clear()
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
        keyboardHelper.init()
        inited = true
    }

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        for (i in 0 .. gameState.buttons.lastIndex) {
            val button = gameState.buttons[i]
            val keyPressed = keyboardHelper.pressedAnyOf(keyMapping[i])
            val toggleAction = button.action is HoldButtonAction && button.action.isToggleControlled
            val shouldBePressed = if (!toggleAction) keyPressed else keyboardHelper.totalPressesOf(keyMapping[i]) % 2 == 1

            if (!button.isPressed && shouldBePressed) {
                return button.hold
            } else if (button.isPressed && !shouldBePressed)
                return button.release
        }
        return null
    }
}
