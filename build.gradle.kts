@file:Suppress("LocalVariableName")

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

group = "org.reckful-archive"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(20)

    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict", // https://kotlinlang.org/docs/java-interop.html#jsr-305-support
            "-Xcontext-receivers"
        )
    }
}

tasks.bootJar {
    enabled = true
}

springBoot {
    mainClass.set("org.reckful.archive.browser.ApplicationKt")
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // web
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("io.github.wimdeblauwe:htmx-spring-boot-thymeleaf:3.1.1")

    // webjars
    implementation("org.webjars:bootstrap:5.3.2")
    implementation("org.webjars.npm:bootstrap-icons:1.11.1")
    implementation("org.webjars.npm:htmx.org:1.9.8")
    implementation("org.webjars:jquery:3.7.1")
    implementation("org.webjars.npm:bootstrap-select:1.14.0-beta3")

    // db
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
