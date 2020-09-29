import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jmailen.kotlinter") version "3.0.2"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "cz.woitee.endlessRunners.evolution"
version = "1.0-SNAPSHOT"
val experimentJar = "ExperimentalMain.jar"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // HashKode has been forked by Gabriel Shanahan to add Gradle and thus work around the invalid JAR bug
    implementation("com.github.gabriel-shanahan:hashkode:latest.integration")

    // XChart
    implementation("org.knowm.xchart:xchart:3.6.5")

    // JUnit5 with KotlinTest
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")

    // This is needed due to a known issue with KotlinTest: https://github.com/kotlintest/kotlintest/issues/639
    testCompile("org.slf4j", "slf4j-simple", "1.7.26")

    implementation(project(":endlessRunnersGame"))

    implementation("io.jenetics:jenetics:4.4.0")
    implementation("io.jenetics:prngine:1.0.2")
    implementation("org.apache.commons:commons-csv:1.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("experimentalMain")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "cz.woitee.endlessRunners.evolution.coevolution.ExperimentalMainKt"))
        }
    }
}
