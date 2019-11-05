package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.IUndoable
import cz.woitee.endlessRunners.game.undoing.NoUndo
import nl.pvdberg.hashkode.compareFields
import nl.pvdberg.hashkode.hashKode

/**
 * A class representing a "virtual" button of the game (which can be mapped to real buttons by a player controller).
 * This is a part of the GameState so we have a better idea on how is the game interacted with from a human perspective.
 */
data class GameButton(val action: GameAction, val gameState: GameState, val index: Int) {
    enum class InteractionType { PRESS, HOLD, RELEASE }
    data class StateChange(val gameButton: GameButton, val interactionType: InteractionType) : IUndoable {
        /**
         * Applies the buttonStateChange on a GameState. PRESS interactions are resolved immediately if applicable and
         * discarded otherwise. HOLD / RELEASE are just forwarded to the GameState which resolves them as soon
         * as they are applicable.
         *
         * A special case are so called "only on press" actions, which act as a press even if they are held.
         */
        fun applyOn(gameState: GameState) {
            when (interactionType) {
                GameButton.InteractionType.PRESS -> {
                    if (!gameButton.isPressed && gameButton.action.isApplicableOn(gameState)) {
                        gameButton.action.applyOn(gameState)
                    }
                }
                GameButton.InteractionType.HOLD -> {
                    if (!gameButton.isPressed) {
                        if (gameButton.action.onlyOnPress && gameButton.action.isApplicableOn(gameState)) {
                            gameButton.action.applyOn(gameState)
                        }
                        gameButton.isPressed = true
                        gameButton.pressedGameTime = gameState.gameTime
                    }
                }
                GameButton.InteractionType.RELEASE -> {
                    gameButton.isPressed = false
                }
            }
        }

        /**
         * Applies the button stateChange on a gameState - undoably. See @applyOn.
         */
        override fun applyUndoablyOn(gameState: GameState): IUndo {
            when (interactionType) {
                GameButton.InteractionType.PRESS -> {
                    if (!gameButton.isPressed && gameButton.action.isApplicableOn(gameState)) {
                        return gameButton.action.applyUndoablyOn(gameState)
                    }
                }
                GameButton.InteractionType.HOLD -> {
                    if (!gameButton.isPressed) {
                        gameButton.isPressed = true
                        val previousPressedGameTime = gameButton.pressedGameTime
                        gameButton.pressedGameTime = gameState.gameTime

                        return object : IUndo {
                            override fun undo(gameState: GameState) {
                                gameButton.pressedGameTime = previousPressedGameTime
                                gameButton.isPressed = false
                            }
                        }
                    }
                }
                GameButton.InteractionType.RELEASE -> {
                    if (gameButton.isPressed) {
                        gameButton.isPressed = false
                        return object : IUndo {
                            override fun undo(gameState: GameState) {
                                gameButton.isPressed = true
                            }
                        }
                    }
                }
            }
            return NoUndo
        }

        override fun toString(): String {
            return "StateChange(${gameButton.index},$interactionType)"
        }

        override fun equals(other: Any?) = compareFields(other) {
            equal = one.gameButton == two.gameButton &&
                    one.interactionType == two.interactionType
        }

        override fun hashCode() = hashKode(gameButton, interactionType)

        companion object {
            fun fromString(gameState: GameState, stringRep: String): StateChange? {
                if (!stringRep.startsWith("StateChange(") && stringRep.endsWith(")"))
                    return null
                val data = stringRep.substring("StateChange(".length .. stringRep.length - 2)
                val split = data.split(',')
                val index = split[0].toInt()
                val interactionType = InteractionType.valueOf(split[1])
                return StateChange(gameState.buttons[index], interactionType)
            }
        }
    }

    var isPressed: Boolean = false
    var pressedGameTime: Double = 0.0

    var press: StateChange = StateChange(this, InteractionType.PRESS)
        protected set
    var hold: StateChange = StateChange(this, InteractionType.HOLD)
        protected set
    var release: StateChange = StateChange(this, InteractionType.RELEASE)
        protected set

    fun interact(interactionType: InteractionType): StateChange {
        return when (interactionType) {
            GameButton.InteractionType.PRESS -> press
            GameButton.InteractionType.HOLD -> hold
            GameButton.InteractionType.RELEASE -> release
        }
    }

    val makesSenseToPress: Boolean
        get() = !isPressed && action.isApplicableOn(gameState)
    val makesSenseToRelease: Boolean
        get() = isPressed && (action !is HoldButtonAction || action.canBeStoppedApplyingOn(gameState))

    /**
     * Very basic equals and hashCode.
     */

    override fun equals(other: Any?) = compareFields(other) {
        equal = one.action.javaClass.simpleName == two.action.javaClass.simpleName
    }

    override fun hashCode() = hashKode(action.javaClass.simpleName)
}
