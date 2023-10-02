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
        // https://kotlinlang.org/docs/java-interop.html#jsr-305-support
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // db
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
