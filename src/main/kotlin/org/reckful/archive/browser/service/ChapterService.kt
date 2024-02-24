package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.repository.VodRepository
import org.reckful.archive.browser.service.WebVttService.WebVttLine
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalTime

@Service
class ChapterService(
    private val vodRepository: VodRepository,
    private val webVttService: WebVttService
) {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun getChaptersWebVttFile(vodId: Long): String {
        val vod = vodRepository.findById(vodId)
            ?.takeIf { it.duration.toHours() < 24 } // TODO add handling later, there's only 4 vods > 24h
            ?: return WebVttService.WEBVTT_EMPTY_RESPONSE

        val chapters = vodRepository.findVodChapters(listOf(vodId))
            .takeIf { it.isNotEmpty() }
            ?.sortedBy { it.startTimeSec }
            ?: return WebVttService.WEBVTT_EMPTY_RESPONSE

        val webVttLines = convertToWebVttLines(chapters, vod.duration)
        return webVttService.createWebVttFile(webVttLines)
    }

    private fun convertToWebVttLines(chapters: List<VodChapterEntity>, vodDuration: Duration): List<WebVttLine> {
        return List(chapters.size) { index ->
            val nextIndex = index + 1
            val nextChapter = if (nextIndex == chapters.size) null else chapters[nextIndex]
            val nextChapterStartTimeSec = nextChapter?.startTimeSec ?: vodDuration.toSeconds()

            val chapter = chapters[index]
            WebVttLine(
                startTime = LocalTime.ofSecondOfDay(chapter.startTimeSec),
                endTime = LocalTime.ofSecondOfDay(nextChapterStartTimeSec),
                title = chapter.name
            )
        }
    }

    companion object {
        const val DEFAULT_CHAPTER_THUMBNAIL_URL =
            "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/chapters/not_found.jpg"
    }
}
