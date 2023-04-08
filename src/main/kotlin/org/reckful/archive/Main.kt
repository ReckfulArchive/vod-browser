package org.reckful.archive

import org.reckful.archive.extractors.ThumbnailExtractor
import org.reckful.archive.parsers.OldArchiveInfoParser
import org.reckful.archive.parsers.TowerCardParser
import java.io.File

// TODO remove hardcoding, extract it to be an environment variable?
private val FILES_DIR = File("/home/ignat/IdeaProjects/twitch-metadata/files").also { check(it.exists()) }

// can be used as a single place to run any scripts in combination with each other
fun main() {
    val towerCards = TowerCardParser(FILES_DIR).getTowerCards()
    val oldArchiveInfo = OldArchiveInfoParser(FILES_DIR).getOldArchiveInfo()

    println("Use for setting a breakpoint")

//    ThumbnailExtractor()
//        .extractThumbnails(
//            towerCards = towerCards,
//            outDir = File("thumbnails")
//        )
}
