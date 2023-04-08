package org.reckful.archive.extractors

import org.reckful.archive.parsers.VideoTowerCard
import java.io.File

class ThumbnailExtractor {

    fun extractThumbnails(towerCards: List<VideoTowerCard>, outDir: File) {
        for (card in towerCards) {
            val thumbnail = card.thumbnail ?: continue

            val videoId = card.link.substringAfterLast("/")
            val extension = thumbnail.extension
            val outputFile = outDir.resolve("video-$videoId.$extension")

            thumbnail.copyTo(outputFile)
        }
    }
}
