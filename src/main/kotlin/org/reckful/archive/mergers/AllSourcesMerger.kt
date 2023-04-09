package org.reckful.archive.mergers

import org.reckful.archive.extractors.ThumbnailExtractor
import org.reckful.archive.parsers.OldArchiveVodInfo
import org.reckful.archive.parsers.VideoInfo
import org.reckful.archive.parsers.VideoTowerCard
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * VODs present in the old vod archive, but not available on twitch as of April 9th, 2023
 *
 * These HAVE TO be VODs, see [AllSourcesMerger.determineType].
 */
private val unavailableVodIds = setOf("290272955", "115841130", "110675493")

private val multipartVideoTitleRegex = Regex(".*\\[\\d/\\d\\].*")

class AllSourcesMerger(
    filesDirectory: File,
    allVideosPageCards: List<VideoTowerCard>,
    highlightsPageCards: List<VideoTowerCard>,
    oldArchiveInfo: List<OldArchiveVodInfo>,
    allVideosInfo: List<VideoInfo>,

    private val thumbnailExtractor: ThumbnailExtractor
) {
    private val allVideosPageCardsById = allVideosPageCards.associateBy { it.link.substringAfterLast("/") }
    private val highlightsPageCardsById = highlightsPageCards.associateBy { it.link.substringAfterLast("/") }
    private val oldArchiveInfoById = oldArchiveInfo.associateBy { it.id.removePrefix("v") }
    private val allVideosInfoById = allVideosInfo.associateBy { it.id.removePrefix("v") }

    // order is important
    private val thumbnailDirectoryPaths = listOf(
        filesDirectory.resolve("thumbnails/640x320"),
        filesDirectory.resolve("thumbnails/320x180")
    ).also { files -> check(files.all { it.exists() }) }

    fun merge(): VideoDatabase {
        val allVideoIds: Set<String> =
            allVideosPageCardsById.keys + highlightsPageCardsById.keys + oldArchiveInfoById.keys + allVideosInfoById.keys

        return VideoDatabase(
            videos = allVideoIds.map { assembleVideo(it) }
        )
    }

    private fun assembleVideo(id: String): Video {
        return Video(
            id = id.toLong(),
            title = getTitle(id),
            description = getDescription(id),
            thumbnail = getLocalThumbnail(id),
            type = determineType(id),
            twitchVideoUrl = getTwitchUrl(id),
            date = getLocalDate(id),
            timestamp = getTimestamp(id),
            duration = getDuration(id),
            categorySegments = getCategorySegments(id)
        )
    }

    private fun getTitle(videoId: String): String {
        val title = if (videoId in unavailableVodIds) {
            oldArchiveInfoById.getValue(videoId).fulltitle
        } else {
            allVideosInfoById.getValue(videoId).fullTitle
        }
        // for some reason the prefix is present in most vods, but it looks like noise
        return title.removePrefix("Reckful - ")
    }

    private fun getDescription(videoId: String): String? {
        return if (videoId in unavailableVodIds) {
            oldArchiveInfoById.getValue(videoId).description
        } else {
            allVideosInfoById.getValue(videoId).description
        }
    }

    private fun getLocalThumbnail(videoId: String): File? {
        return thumbnailExtractor.findByVideoId(videoId, thumbnailDirectoryPaths)
    }

    private fun determineType(videoId: String): VideoType {
        if (videoId in unavailableVodIds) {
            return VideoType.VOD
        }

        val videoInfo = allVideosInfoById.getValue(videoId)

        @Suppress("DEPRECATION")
        val isArchivedVod = videoInfo.description?.toLowerCase()?.let {
            it.contains("archived")
                    || it.contains("highlighting so it does")
                    || it.contains("highlighting this so it does")
        } == true

        if (isArchivedVod) {
            return VideoType.VOD
        }


        val isDuplicateByTitle = allVideosInfoById.values.any {
            it.title == videoInfo.title && it.id != videoInfo.id
        }
        // the stream was probably restarted, so good chances it's a VOD
        if (isDuplicateByTitle) {
            return VideoType.VOD_MAYBE
        }

        val isMultipart = videoInfo.title.matches(multipartVideoTitleRegex)
        if (isMultipart) {
            return VideoType.VOD_MAYBE
        }

        val isLessThanTenMinutes = (videoInfo.duration ?: 0) < 600
        if (isLessThanTenMinutes) {
            return VideoType.HIGHLIGHT_MAYBE
        }

        // contains both vods and highlights of the time
        val existsInOldArchive = oldArchiveInfoById[videoId] != null
        if (existsInOldArchive) {
            return VideoType.VOD_MAYBE
        }

        val isAmongHighlights = highlightsPageCardsById[videoId] != null
        if (isAmongHighlights) {
            return VideoType.HIGHLIGHT
        }

        // if it's not an old vod, but it's not among the highlights,
        // it must be a vod that wasn't downloaded
        return VideoType.VOD
    }

    private fun getTwitchUrl(videoId: String): String? {
        return if (videoId in unavailableVodIds) {
            null
        } else {
            allVideosInfoById.getValue(videoId).webpageUrl
        }
    }

    private fun getLocalDate(videoId: String): LocalDate {
        val timestamp = getTimestamp(videoId)
        return toAustinLocalDate(timestamp)
    }

    private fun getTimestamp(videoId: String): Long {
        return if (videoId in unavailableVodIds) {
            oldArchiveInfoById.getValue(videoId).timestamp
        } else {
            allVideosInfoById.getValue(videoId).timestamp
        }
    }

    /**
     * Maps the video timestamp to the local date in Austin, Texas.
     *
     * Even though it may be wrong for Japan streams, I assume that's where
     * the majority of streams were made, and we need to have some human-readable date.
     *
     * To get the exact date/time at a certain location (such as Japan), use [VideoInfo.timestamp].
     */
    private fun toAustinLocalDate(timestamp: Long): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp * 1000L)
        return LocalDate.ofInstant(instant, ZoneId.of("CST6CDT"))
    }

    private fun getDuration(videoId: String): Duration {
        val duration = if (videoId in unavailableVodIds) {
            oldArchiveInfoById[videoId]?.duration
        } else {
            allVideosInfoById[videoId]?.duration
        } ?: 0

        return Duration.ofSeconds(duration.toLong())
    }

    private fun getCategorySegments(videoId: String): List<CategorySegment> {
        if (videoId in unavailableVodIds) {
            // no such information in the old dump
            return emptyList()
        }

        val videoInfo = allVideosInfoById.getValue(videoId)
        return videoInfo.chapters.map {
            CategorySegment(
                categoryName = it.title,
                startTimeSeconds = it.startTime,
                endTimeSeconds = it.endTime ?: -1
            )
        }
    }
}

data class VideoDatabase(
    val videos: List<Video>
)

data class Video(
    val id: Long,

    val title: String,

    /**
     * Not sure if it's present in VODs, but might be present in highlights.
     * This field could be used to describe VODs manually though, IMDB-style.
     */
    val description: String?,

    /**
     * Thumbnail image with the best resolution possible for this video.
     * The file must be located in this repository.
     */
    val thumbnail: File?,

    val type: VideoType,

    val twitchVideoUrl: String?,

    val date: LocalDate,

    /**
     * Even though there is [date], the timestamp could be used
     * to determine which of the two same-day streams started earlier.
     * Some streams are broken into parts due restarts.
     */
    val timestamp: Long,

    val duration: Duration,

    val categorySegments: List<CategorySegment>
)

enum class VideoType {
    VOD,
    VOD_MAYBE,
    HIGHLIGHT,
    HIGHLIGHT_MAYBE
}

data class CategorySegment(
    val categoryName: String,

    /**
     * Relative to the stream timeline
     */
    val startTimeSeconds: Int,

    /**
     * Relative to the stream timeline
     *
     * The value is `-1` if the end is not defined
     */
    val endTimeSeconds: Int
)
