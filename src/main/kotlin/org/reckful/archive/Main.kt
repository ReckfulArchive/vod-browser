package org.reckful.archive

import org.reckful.archive.extractors.ThumbnailExtractor
import org.reckful.archive.mergers.AllSourcesMerger
import org.reckful.archive.parsers.AllVideosInfoParser
import org.reckful.archive.parsers.AllVideosPageParser
import org.reckful.archive.parsers.HighlightsPageParser
import org.reckful.archive.parsers.OldArchiveInfoParser
import java.io.File

// TODO remove hardcoding, extract it to be an environment variable?
private val filesDir = File("/home/ignat/IdeaProjects/twitch-metadata/files").also { check(it.exists()) }

// can be used as a single place to run any scripts in combination with each other
fun main() {
    val allVideoCards = AllVideosPageParser(filesDir).getTowerCards()
    val highlightsCards = HighlightsPageParser(filesDir).getTowerCards()
    val oldArchiveInfo = OldArchiveInfoParser(filesDir).getOldArchiveInfo()
    val allVideosInfo = AllVideosInfoParser(filesDir).getVideosInfo()
    val thumbnailExtractor = ThumbnailExtractor()

    val mergedInfo = AllSourcesMerger(
        filesDirectory = filesDir,
        allVideosPageCards = allVideoCards,
        highlightsPageCards = highlightsCards,
        oldArchiveInfo = oldArchiveInfo,
        allVideosInfo = allVideosInfo,
        thumbnailExtractor = thumbnailExtractor
    ).merge()

//    thumbnailExtractor.extractFromTowerCards(
//        towerCards = allVideoCards,
//        outDir = filesDir.resolve("thumbnails/320x180")
//    )
//    thumbnailExtractor.extractFromOldArchiveInfo(
//        oldArchiveInfo = oldArchiveInfo,
//        outDir = filesDir.resolve("thumbnails/640x320")
//    )

    println("Boop")
}
