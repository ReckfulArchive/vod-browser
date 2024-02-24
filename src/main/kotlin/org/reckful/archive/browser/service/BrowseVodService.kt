package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.mapper.ChapterMapper
import org.reckful.archive.browser.model.Page
import org.reckful.archive.browser.model.browse.*
import org.reckful.archive.browser.model.db.SortOrder
import org.reckful.archive.browser.model.vod.DateTimeVodSortOption
import org.reckful.archive.browser.model.vod.DurationVodSortOption
import org.reckful.archive.browser.model.vod.RandomStableVodSortOption
import org.reckful.archive.browser.repository.VodRepository
import org.reckful.archive.browser.service.ChapterService.Companion.DEFAULT_CHAPTER_THUMBNAIL_URL
import org.reckful.archive.browser.util.findWithLongestDurationGroupedBy
import org.reckful.archive.browser.util.formatToDoubleNumber
import org.reckful.archive.browser.util.formatToHumanReadable
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

interface BrowseVodService {

    fun getChapterFilterOptions(
        title: String? = null,
        year: Int? = null
    ): List<BrowseFilterChapter>

    fun getYearFilterOptions(
        title: String? = null,
        chapterId: Long? = null,
    ): List<BrowseFilterYear>

    fun getPage(
        title: String? = null,
        chapterId: Long? = null,
        year: Int? = null,
        sort: BrowseFilterSort = DateDescBrowseFilterSort,
        page: Int,
        limit: Int
    ): Page<BrowseVod>
}

@Service
class PersistentBrowseVodService(
    private val chapterMapper: ChapterMapper,
    private val vodRepository: VodRepository
) : BrowseVodService {

    override fun getChapterFilterOptions(
        title: String?,
        year: Int?
    ): List<BrowseFilterChapter> {
        return vodRepository.countChapterVods(
            playlistIds = listOf(10L),
            likeTitle = title?.takeIf { it.isNotBlank() },
            year = year
        ).map {
            BrowseFilterChapter(
                chapterId = it.chapterId,
                chapterName = it.chapterName,
                vodCount = it.vodCount
            )
        }
    }

    override fun getYearFilterOptions(
        title: String?,
        chapterId: Long?,
    ): List<BrowseFilterYear> {
        return vodRepository.countYearVods(
            playlistIds = listOf(10),
            likeTitle = title?.takeIf { it.isNotBlank() },
            chapterId = chapterId
        ).map {
            BrowseFilterYear(
                yearValue = it.year,
                vodCount = it.vodCount
            )
        }
    }

    override fun getPage(
        title: String?,
        chapterId: Long?,
        year: Int?,
        sort: BrowseFilterSort,
        page: Int,
        limit: Int
    ): Page<BrowseVod> {
        val sortBy = when (sort) {
            DateDescBrowseFilterSort, DateAscBrowseFilterSort -> DateTimeVodSortOption
            DurationDescBrowseFilterSort, DurationAscBrowseFilterSort -> DurationVodSortOption
            is RandomStableBrowseFilterSort -> RandomStableVodSortOption(sort.seed)
        }

        val sortOrder = when (sort) {
            DateDescBrowseFilterSort, DurationDescBrowseFilterSort -> SortOrder.DESC
            DateAscBrowseFilterSort, DurationAscBrowseFilterSort -> SortOrder.ASC
            is RandomStableBrowseFilterSort -> SortOrder.ASC
        }

        val vods = vodRepository.findVods(
            playlistIds = listOf(10),
            likeTitle = title?.takeIf { it.isNotBlank() },
            chapterId = chapterId,
            year = year,
            sortBy = sortBy,
            sortOrder = sortOrder,
            page = page,
            limit = limit
        )

        val vodIds = vods.map { it.id }
        val chapters = vodRepository.findVodChapters(vodIds).groupBy { it.vodId }

        val pageVods = vods.map {
            it.toBrowseVod(
                chapters = chapters[it.id] ?: emptyList(),
            )
        }
        return Page(
            data = pageVods,
            pageNum = page,
            limit = limit
        )
    }

    private fun VodEntity.toBrowseVod(chapters: List<VodChapterEntity>): BrowseVod {
        val chaptersWithDuration = chapterMapper.withDuration(
            chapters = chapters,
            vodDuration = this.duration
        ) { chapter, duration ->
            BrowseVodChapter(
                name = chapter.name,
                thumbnailUrl = chapter.thumbnailUrl,
                duration = duration.formatToHumanReadable(),
                startTimeSec = chapter.startTimeSec.toInt()
            )
        }

        val primaryChapterThumbnailUrl = chaptersWithDuration
            .findWithLongestDurationGroupedBy { it.name }
            ?.thumbnailUrl
            ?: DEFAULT_CHAPTER_THUMBNAIL_URL

        return BrowseVod(
            id = this.id,
            title = this.title,
            date = this.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            duration = this.duration.formatToDoubleNumber(),
            vodThumbnailUrl = this.thumbnailUrl,
            primaryChapterThumbnailUrl = primaryChapterThumbnailUrl,
            chapters = chaptersWithDuration.map { it.data }
        )
    }
}

