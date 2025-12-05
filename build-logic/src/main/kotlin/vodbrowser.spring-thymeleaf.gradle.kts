plugins {
    id("vodbrowser.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")

    // Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation(libs.htmx.spring.boot.thymeleaf)

    // Webjars
    implementation(libs.webjars.bootstrap)
    implementation(libs.webjars.bootstrapIcons)
    implementation(libs.webjars.htmx)

    implementation(libs.webjars.bootstrapSelect)
    implementation(libs.webjars.jquery)
}
