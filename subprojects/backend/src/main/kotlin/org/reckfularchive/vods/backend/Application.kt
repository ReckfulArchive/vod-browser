package org.reckfularchive.vods.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main() {
    runApplication<Application>()
}

@SpringBootApplication(scanBasePackages = ["org.reckfularchive.vods"])
class Application
