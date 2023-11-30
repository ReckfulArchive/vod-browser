package org.reckful.archive.browser.mapper

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.util.WithDuration
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ChapterMapper {

    fun <T> withDuration(
        chapters: List<VodChapterEntity>,
        vodDuration: Duration,
        chapterMapper: (chapter: VodChapterEntity, duration: Duration) -> T
    ): List<WithDuration<T>> {
        if (chapters.isEmpty()) {
            return emptyList()
        } else if (chapters.size == 1) {
            val singleChapter = chapters[0]
            val singleVodDuration = vodDuration.minusSeconds(singleChapter.startTimeSec)
            return listOf(
                WithDuration(
                    data = chapterMapper(singleChapter, singleVodDuration),
                    duration = singleVodDuration
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

            WithDuration(
                data = chapterMapper(chapter, chapterDuration),
                duration = chapterDuration
            )
        }
    }
}


