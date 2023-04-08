plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

group = "org.reckful-archive"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jsoup:jsoup:1.15.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

