package org.reckful.archive.extractors

import org.reckful.archive.parsers.OldArchiveTwitchVodInfo
import org.reckful.archive.parsers.VideoTowerCard
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class ThumbnailExtractor {

    fun extractFromTowerCards(towerCards: List<VideoTowerCard>, outDir: File) {
        for (card in towerCards) {
            val thumbnail = card.thumbnail ?: continue

            val videoId = card.link.substringAfterLast("/")
            val extension = thumbnail.extension
            val outputFile = outDir.resolve("video-$videoId.$extension")
            if (!outputFile.exists()) {
                thumbnail.copyTo(outputFile)
            }
        }
    }

    fun extractFromOldArchiveInfo(oldArchiveInfo: List<OldArchiveTwitchVodInfo>, outDir: File) {
        oldArchiveInfo.forEachIndexed { index, oldVodInfo ->
            val bestThumbnailUrl = getBestThumbnailUrl(oldVodInfo) ?: return@forEachIndexed

            val videoId = oldVodInfo.id.removePrefix("v")
            val extension = bestThumbnailUrl.substringAfterLast(".")
            val outputFile = outDir.resolve("video-$videoId.$extension")

            if (!outputFile.exists()) {
                println("Downloading image #${index + 1} out of ${oldArchiveInfo.size}")
                val image = try {
                    ImageIO.read(URL(bestThumbnailUrl))
                } catch (e: Exception) { // some links give 404
                    println("Unable to read image $bestThumbnailUrl: $e)")
                    return@forEachIndexed
                }
                ImageIO.write(image, extension, outputFile)
            }
        }
    }

    private fun getBestThumbnailUrl(vodInfo: OldArchiveTwitchVodInfo): String? {
        val primaryUrl = vodInfo.thumbnail
        val maxPreferenceUrl = vodInfo.thumbnails.maxBy { it.preference }.url
        if (primaryUrl != maxPreferenceUrl) {
            throw IllegalStateException("Mismatch in primary url and max preference: $primaryUrl vs $maxPreferenceUrl")
        }

        if (primaryUrl == "https://vod-secure.twitch.tv/_404/404_processing_640x360.png") {
            return null
        }

        val isExpectedResolution = primaryUrl.substringAfterLast("/").contains("640x360")
        if (!isExpectedResolution) {
            throw IllegalStateException("Unexpected resolution for $primaryUrl")
        }

        val isExpectedFormat = primaryUrl.endsWith(".jpg") || primaryUrl.endsWith(".jpeg")
        if (!isExpectedFormat) {
            throw IllegalStateException("Unexpected format for $primaryUrl")
        }

        return primaryUrl
    }
}
