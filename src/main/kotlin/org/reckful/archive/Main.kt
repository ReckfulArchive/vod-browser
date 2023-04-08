package org.reckful.archive

import org.reckful.archive.extractors.ThumbnailExtractor
import org.reckful.archive.parsers.TowerCardParser
import java.io.File

// can be used as a single place to run any scripts in combination with each other
fun main() {
    val towerCards = TowerCardParser().getTowerCards()
    ThumbnailExtractor()
        .extractThumbnails(
            towerCards = towerCards,
            outDir = File("thumbnails")
        )
}
