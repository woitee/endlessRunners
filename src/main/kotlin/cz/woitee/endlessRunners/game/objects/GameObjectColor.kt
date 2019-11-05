package cz.woitee.endlessRunners.game.objects

import java.awt.Color

/**
 * An enumeration class, depicting the general game object colors.
 */
enum class GameObjectColor(val ord: Int, val awtColor: Color) {
    UNSPECIFIED(0, Color.LIGHT_GRAY), BLUE(1, Color.BLUE),
    GREEN(2, Color.GREEN), RED(3, Color.RED), YELLOW(4, Color.YELLOW), ORANGE(5, Color.ORANGE),
    LIGHTBLUE(6, Color(66, 137, 244));

    companion object {
        private val ordMap = GameObjectColor.values().associateBy(GameObjectColor::ord)
        private val awtColorMap = GameObjectColor.values().associateBy(GameObjectColor::awtColor)
        fun fromInt(ord: Int) = ordMap[ord]!!
        fun fromAwtColor(color: Color) = awtColorMap[color]!!
    }
}
