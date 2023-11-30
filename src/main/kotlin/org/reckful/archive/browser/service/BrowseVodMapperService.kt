package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.model.browse.BrowseVod
import org.reckful.archive.browser.model.browse.BrowseVodChapter
import org.reckful.archive.browser.util.WithDuration
import org.reckful.archive.browser.util.findWithLongestDurationGroupedBy
import org.reckful.archive.browser.util.formatToDoubleNumber
import org.reckful.archive.browser.util.formatToHumanReadable
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.time.Duration
import java.time.format.DateTimeFormatter

@Service
class BrowseVodMapperService {

    fun mapToDTO(
        vod: VodEntity,
        chapters: List<VodChapterEntity>,
        mirrors: List<VodMirrorEntity>
    ): BrowseVod {
        val chaptersWithDuration = mapChapters(chapters, vodDuration = vod.duration)
        val primaryChapterThumbnailUrl = chaptersWithDuration
            .findWithLongestDurationGroupedBy { it.name }
            ?.thumbnailUrl
            ?: DEFAULT_CHAPTER_THUMBNAIL_URL

        return BrowseVod(
            id = vod.id,
            title = vod.title,
            date = vod.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            duration = vod.duration.formatToDoubleNumber(),
            vodThumbnailUrl = vod.thumbnailUrl,
            primaryChapterThumbnailUrl = primaryChapterThumbnailUrl,
            chapters = chaptersWithDuration.map { it.data },
            vodUrl = getUrl(vod, mirrors) ?: "#",
        )
    }

    private fun mapChapters(
        chapters: List<VodChapterEntity>,
        vodDuration: Duration
    ): List<WithDuration<BrowseVodChapter>> {
        if (chapters.isEmpty()) {
            return emptyList()
        } else if (chapters.size == 1) {
            val singleChapter = chapters[0]
            return listOf(
                mapChapter(
                    chapter = singleChapter,
                    duration = vodDuration.minusSeconds(singleChapter.startTimeSec)
                )
            )
        }

        val sortedChapters = chapters.sortedBy { it.startTimeSec }
        return List(sortedChapters.size) { index ->
            val chapter = sortedChapters[index]

            val nextIndex = index + 1
            val nextChapter = if (nextIndex == chapters.size) null else sortedChapters[nextIndex]
            val nextChapterStartTimeSec = nextChapter?.startTimeSec ?: vodDuration.toSeconds()

            val chapterDuration = Duration.ofSeconds(nextChapterStartTimeSec - chapter.startTimeSec)
            mapChapter(
                chapter = chapter,
                duration = chapterDuration
            )
        }
    }

    private fun mapChapter(chapter: VodChapterEntity, duration: Duration): WithDuration<BrowseVodChapter> {
        return WithDuration(
            data = BrowseVodChapter(
                name = chapter.name,
                thumbnailUrl = chapter.thumbnailUrl,
                duration = duration.formatToHumanReadable(),
                startTimeSec = chapter.startTimeSec.toInt()
            ),
            duration = duration
        )
    }

    private fun getUrl(vod: VodEntity, mirrors: List<VodMirrorEntity>): String? {
        return getArchiveVideoFileUrl(mirrors)
            ?: return vod.getTwitchId()?.let { "https://www.twitch.tv/videos/$it" }
    }

    fun getArchiveVideoFileUrl(mirrors: List<VodMirrorEntity>): String? {
        val archiveMirror = mirrors.firstOrNull { it.type == "ARCHIVE_FILE_PATH" } ?: return null
        val archivePath = URLEncoder.encode(archiveMirror.url, "UTF-8").replace("+", "%20")
        return "https://files.reckful-archive.org/$archivePath"
    }

    private fun VodEntity.getTwitchId(): String? {
        return when {
            this.externalId.startsWith("twitch:") -> this.externalId.removePrefix("twitch:")
            else -> null
        }
    }

    private companion object {
        private const val DEFAULT_CHAPTER_THUMBNAIL_URL =
            "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/chapters/not_found.jpg"
    }
}


