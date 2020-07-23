group = "cz.woitee.endlessRunnersBulk"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.50" apply false
}

subprojects {
    version = "1.0"
    apply(plugin="kotlin")
}