rootProject.name = "endlessRunnersBulk"

include("endlessRunnersGame")

sourceControl {
    gitRepository(uri("https://github.com/gabriel-shanahan/HashKode.git")) {
        producesModule("com.github.gabriel-shanahan:hashkode")
    }
}

include("endlessRunnersEvo")
