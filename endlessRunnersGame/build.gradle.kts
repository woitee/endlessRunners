plugins {
    kotlin("jvm") version "1.3.50"

    id("org.jetbrains.dokka") version "0.9.18"
    id("org.jmailen.kotlinter") version "2.1.1"
}

group = "cz.woitee.endlessRunners"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // HashKode has been forked by Gabriel Shanahan to add Gradle and thus work around the invalid JAR bug
    implementation("com.github.gabriel-shanahan:hashkode:latest.integration")

    // JUnit5 with KotlinTest
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")

    // This is needed due to a known issue with KotlinTest: https://github.com/kotlintest/kotlintest/issues/639
    testCompile("org.slf4j", "slf4j-simple", "1.7.26")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        // Make sure output from
        // standard out or error is shown
        // in Gradle output.
        showStandardStreams = true
    }
}
