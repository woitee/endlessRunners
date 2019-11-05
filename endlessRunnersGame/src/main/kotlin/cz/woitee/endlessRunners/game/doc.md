# Package cz.woitee.endlessRunners.game 

The actual package containing the endless runner framework itself, with many details.

# Package cz.woitee.endlessRunners.game.actions

Action of player in an endless runner game.

# Package cz.woitee.endlessRunners.game.actions.abstract

Abstract structure of actions

# Package cz.woitee.endlessRunners.game.actions.composite

Actions that provide compositing one action from multiple (such as by a condition).

# Package cz.woitee.endlessRunners.game.algorithms.dfs

An implementation of the DFS algorithm with helper classes for statistics and caching.

# Package cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin

Classes used for the implementation of the DelayedTwinDFS algorithm.

# Package cz.woitee.endlessRunners.game.collisions

Collisions that can occur in the game, methods for their detection and handling

# Package cz.woitee.endlessRunners.game.collisions.collisionEffects

Descriptions of possible outcomes of objects colliding with each other.

# Package cz.woitee.endlessRunners.game.collisions.collisionEffects.composite

Composite collision effects made by compositing other collisionEffects.

# Package cz.woitee.endlessRunners.game.conditions

Game condtions that can be true or false in a given GameState

# Package cz.woitee.endlessRunners.game.descriptions

Descriptions for games (i.e. their mechanics) made in our framework

# Package cz.woitee.endlessRunners.game.descriptions.imitators

Game descriptions imitating real endless runner games

# Package cz.woitee.endlessRunners.game.effects

Effects are basic events that can happen in the game

# Package cz.woitee.endlessRunners.game.effects.composite

Composite effects made by assembling other effects.

# Package cz.woitee.endlessRunners.game.gui

Everything considering a graphical representation of what can happen in a game, tools for rendering and screenshots.

# Package cz.woitee.endlessRunners.game.levelGenerators

Classes for generating level content for a game

# Package cz.woitee.endlessRunners.game.levelGenerators.block

Block approach to generating levels, piecing the playthrough together from spatially separated chunks.

# Package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

Wrappers around other levelGenerators, giving them additional attributes, such as ensuring the level is always survivable.

# Package cz.woitee.endlessRunners.game.objects

Implementations of any objects in games

# Package cz.woitee.endlessRunners.game.playerControllers

Different objects that control the behaviour of the player in games.

# Package cz.woitee.endlessRunners.game.tracking

Objects for tracking the game and providing statistics of its features.

# Package cz.woitee.endlessRunners.game.undoing

Undoing capabilities - objects that can be returned after game effects, actions happen and further used to return
to the original state. 