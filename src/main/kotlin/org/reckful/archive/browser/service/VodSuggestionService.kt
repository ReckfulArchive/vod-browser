package org.reckful.archive.browser.service

import org.reckful.archive.browser.mapper.ChapterMapper
import org.reckful.archive.browser.model.Page
import org.reckful.archive.browser.model.vod.RandomStableVodSortOption
import org.reckful.archive.browser.model.vod.VodSuggestion
import org.reckful.archive.browser.repository.VodRepository
import org.reckful.archive.browser.util.IdHolder
import org.reckful.archive.browser.util.findWithLongestDurationGroupedBy
import org.reckful.archive.browser.util.formatToDoubleNumber
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

interface VodSuggestionService {
    fun getNextByDate(vodId: Long, page: Int, limit: Int): Page<VodSuggestion>

    fun getMoreLike(vodId: Long, seed: Int, page: Int, limit: Int): Page<VodSuggestion>
}

@Service
class PersistentVodSuggestionService(
    private val chapterMapper: ChapterMapper,
    private val vodRepository: VodRepository
) : VodSuggestionService {

    override fun getNextByDate(vodId: Long, page: Int, limit: Int): Page<VodSuggestion> {
        val vodSuggestions = vodRepository.findNextByDate(
            vodId = vodId,
            playlistIds = listOf(10),
            page = page,
            limit = limit
        ).map {
            VodSuggestion(
                vodId = it.id,
                thumbnailUrl = it.thumbnailUrl,
                title = it.title,
                date = it.dateTime.format(PREVIEW_DATE_FORMATTER),
                duration = it.duration.formatToDoubleNumber()
            )
        }

        return Page(
            data = vodSuggestions,
            pageNum = page,
            limit = limit
        )
    }

    override fun getMoreLike(vodId: Long, seed: Int, page: Int, limit: Int): Page<VodSuggestion> {
        val vodChapters = vodRepository.findVodChapters(listOf(vodId))
        if (vodChapters.isEmpty()) {
            return Page(
                data = emptyList(),
                pageNum = page,
                limit = limit
            )
        }

        val vod = requireNotNull(vodRepository.findById(vodId)) {
            "Vod with id $vodId is not found"
        }

        val vodChaptersWithDuration = chapterMapper.withDuration(
            chapters = vodChapters,
            vodDuration = vod.duration
        ) { vodChapterEntity, _ ->
            IdHolder(vodChapterEntity.chapterId)
        }

        val primaryChapterId = vodChaptersWithDuration
            .findWithLongestDurationGroupedBy { it.id }
            ?.id
            ?: error("Chapters are not empty, so this should never happend")

        // TODO the current VOD might be in the list of suggestions, address it
        val suggestions = vodRepository.findVods(
            playlistIds = listOf(10),
            chapterId = primaryChapterId,
            sortBy = RandomStableVodSortOption(seed),
            page = page,
            limit = limit
        ).map {
            VodSuggestion(
                vodId = it.id,
                thumbnailUrl = it.thumbnailUrl,
                title = it.title,
                date = it.dateTime.format(PREVIEW_DATE_FORMATTER),
                duration = it.duration.formatToDoubleNumber()
            )
        }

        return Page(
            data = suggestions,
            pageNum = page,
            limit = limit
        )
    }

    private companion object {
        private val PREVIEW_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
