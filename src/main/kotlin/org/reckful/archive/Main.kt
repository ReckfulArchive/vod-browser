package org.reckful.archive

import org.reckful.archive.extractors.ThumbnailExtractor
import org.reckful.archive.parsers.AllVideosInfoParser
import org.reckful.archive.parsers.OldArchiveInfoParser
import org.reckful.archive.parsers.TowerCardParser
import java.io.File

// TODO remove hardcoding, extract it to be an environment variable?
private val filesDir = File("/home/ignat/IdeaProjects/twitch-metadata/files").also { check(it.exists()) }

// can be used as a single place to run any scripts in combination with each other
fun main() {
    val towerCards = TowerCardParser(filesDir).getTowerCards()
    val oldArchiveInfo = OldArchiveInfoParser(filesDir).getOldArchiveInfo()
    val vodsInfo = AllVideosInfoParser(filesDir).getVodInfo()

    val thumbnailExtractor = ThumbnailExtractor()

    thumbnailExtractor.extractFromTowerCards(
        towerCards = towerCards,
        outDir = filesDir.resolve("thumbnails/320x180")
    )

    thumbnailExtractor.extractFromOldArchiveInfo(
        oldArchiveInfo = oldArchiveInfo,
        outDir = filesDir.resolve("thumbnails/640x320")
    )

    println("Boop")
}
