package org.reckfularchive.browser.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main() {
    runApplication<Application>()
}

@SpringBootApplication(scanBasePackages = ["org.reckfularchive.browser"])
class Application
