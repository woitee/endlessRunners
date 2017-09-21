package game.gameObjects

import java.awt.Color

enum class GameObjectColor(val ord: Int, val color: Color) {
    UNSPECIFIED(0, Color.LIGHT_GRAY), BLUE(1, Color.BLUE),
    GREEN(2, Color.GREEN), RED(3, Color.RED), YELLOW(4, Color.YELLOW), ORANGE(5, Color.ORANGE);

    companion object {
        private val ordMap = GameObjectColor.values().associateBy(GameObjectColor::ord)
        private val awtColorMap = GameObjectColor.values().associateBy(GameObjectColor::color)
        fun fromInt(ord: Int) = ordMap[ord]!!
        fun fromAwtColor(color: Color) = awtColorMap[color]!!
    }
}