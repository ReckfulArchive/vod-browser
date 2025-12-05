import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("vodbrowser.kotlin-jvm")

    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        // For explanation of -Xjsr305=strict, see
        // - https://kotlinlang.org/docs/java-interop.html#jsr-305-support
        // - https://docs.spring.io/spring-boot/reference/features/kotlin.html#features.kotlin.null-safety
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

// Subprojects aren't runnable by default,
// one app module builds an executable
tasks.bootJar {
    enabled = false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
