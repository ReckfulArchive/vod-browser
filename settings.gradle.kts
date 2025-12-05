@file:Suppress("UnstableApiUsage")

rootProject.name = "vod-browser"

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        maven(url = "https://repo.spring.io/milestone")
        maven(url = "https://repo.spring.io/snapshot")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

include(
    ":subprojects:backend",
    ":subprojects:website",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
