package cz.woitee.endlessRunners.game.playerControllers.wrappers

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.playerControllers.PlayerController

class DisplayingWrapper(val innerController: PlayerController) : PlayerController() {
    override fun init(gameState: GameState) = innerController.init(gameState)

    override fun onUpdate(gameState: GameState) = innerController.onUpdate(gameState).also {
        val visualizer = gameState.game.visualizer as? GamePanelVisualizer ?: return@also

        val text = gameState.buttons
            .withIndex()
            .filter { (_, button) -> button.isPressed }
            .joinToString { (i, _) -> i.toString() }

        visualizer.debugText.text = text
    }
}
