package cz.woitee.endlessRunners.game.gui

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.BlockValidator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * An object to save screenshots of gameplay or HeightBlocks.
 */
object ScreenshotSaver {
    /**
     * Save screenshot of a HeightBlock.
     *
     * @param heightBlock The HeightBlock to screenshot
     * @param filename The filename to save it to
     */
    fun saveScreenshot(heightBlock: HeightBlock, filename: String) {
        val gameState = BlockValidator(GameDescription(), { NoActionPlayerController() }).getBlockAsGameState(heightBlock)
        gameState.addToGrid(Player(), heightBlock.width - 1, heightBlock.endHeight + 1)

        saveScreenshot(gameState, filename,
                playerScreenX = 0.0,
                endX = heightBlock.width,
                endY = heightBlock.height
        )
    }

    /**
     * Save screenshot of a full, or a portion of a GameState
     *
     * @param gameState The GameState
     * @param filename The filename to save this to
     * @param startX The starting X coordinate from which to draw the state
     * @param startY The starting Y coordinate from which to draw the state
     * @param endX The ending X coordinate to which to draw the state
     * @param endY The ending Y coordinate to which to draw the state
     * @param playerScreenX The player position on the screen
     */
    fun saveScreenshot(
        gameState: GameState,
        filename: String,
        startX: Int = 0,
        startY: Int = 0,
        endX: Int = gameState.grid.width,
        endY: Int = gameState.grid.height,
        playerScreenX: Double = PlayerScreenX
    ) {

        val width = endX - startX
        val height = endY - startY
        val widthPx = width * BlockWidth
        val heightPx = height * BlockHeight

        val gamePanelVisualizer = GamePanelVisualizer(
                width = widthPx,
                height = heightPx,
                showFrame = false,
                playerScreenX = playerScreenX
        )

        saveScreenshot(gameState, gamePanelVisualizer, filename)
        gamePanelVisualizer.dispose()
    }

    /**
     * Save a screenshot as currently drawn by a given GamePanelVisualizer. Useful if the Visualizer is currently
     * drawing e.g. debugObjects
     *
     * @param gameState The GameState to screenshot
     * @param gamePanelVisualizer GamePanelVisualizer used for screenshoting
     * @param filename Target filename of the screenshot
     */
    fun saveScreenshot(gameState: GameState, gamePanelVisualizer: GamePanelVisualizer, filename: String) {
        val image = BufferedImage(gamePanelVisualizer.width, gamePanelVisualizer.height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.color = Color.WHITE
        graphics.background = Color.WHITE
        graphics.fillRect(0, 0, gamePanelVisualizer.width, gamePanelVisualizer.height)

        gamePanelVisualizer.drawEverything(gameState, graphics)

        val file = File("out/$filename")
        file.parentFile.mkdirs()
        if (ImageIO.write(image, "png", file)) {
            println("Saved screenshot to $filename")
        } else {
            println("Failed generating screenshot")
        }
    }
}
