
# Endless Runners project

This is a project that aims to help prototype / generate various endless runner games.

It has several entry points:

- `endlessRunnersGame/src/main/kotlin/cz/woitee/endlessRunners/Main.kt` - configurable main method allowing to start a game of your choosing
- `endlessRunnersGame/src/main/kotlin/cz/woitee/endlessRunners/ManualTests.kt` - runs a test that is assessed by looking at it
- `endlessRunnersGame/src/main/kotlin/cz/woitee/endlessRunners/PlayWithReplay.kt` - play the game and save a replay
- `endlessRunnersGame/src/main/kotlin/cz/woitee/endlessRunners/PlayReplay.kt` - play a replay from a file

- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/SwingLauncherMain.kt` - runs a GUI launcher that allows running a specific version of the game / evolution experiment result

- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/evoBlock/SingleEvoBlockMain.kt` - evolves a single-block (part of level generator)
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/evoBlock/MultiEvoBlockMain.kt` - evolves multiple blocks (full level generator)
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/evoController/EvoControllerMain.kt` - evolves a controller for a game
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/evoGame/EvoGameMain.kt` - evolves a game ruleset
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/evoGame/EvoGameReplayMain.kt` - replays a previous result of ruleset generation
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/grandEvo/GrandEvoMain.kt` - evolves level generator and controller simultaneously
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/coevolution/CoevolutionMain.kt` - simply evolves all 3 - ruleset, level generator and controller
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/evolution/coevolution/ExperimentalMain.kt` - precisely configured all 3 evolution, fine tuned for academic research

- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/experiments/Experiment1Main.kt` - experiment that was used to evaluate two versions of AI as game validators. Used in the author's [Master thesis](https://dspace.cuni.cz/handle/20.500.11956/101879)
- `endlessRunnersEvo/src/main/kotlin/cz/woitee/endlessRunners/experiments/Experiment1BaselineRuns.kt` - helper binary to set baselines for the two games - how far do AI players get
