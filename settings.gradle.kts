@file:Suppress("LocalVariableName")

rootProject.name = "vod-browser-server"

pluginManagement {
    plugins {
        val kotlin_version: String by settings
        val spring_boot_version: String by settings
        val spring_dependency_management_version: String by settings

        kotlin("jvm") version kotlin_version
        kotlin("plugin.serialization") version kotlin_version

        id("org.springframework.boot") version spring_boot_version
        id("io.spring.dependency-management") version spring_dependency_management_version
        kotlin("plugin.spring") version kotlin_version
    }
}
