plugins {
    id("vodbrowser.spring-web")
    // id("vodbrowser.spring-persistence")
}

tasks.bootJar {
    enabled = true
}

springBoot {
    mainClass.set("org.reckfularchive.browser.backend.ApplicationKt")
}

dependencies {
    implementation(projects.subprojects.website)
}
