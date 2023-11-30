package org.reckful.archive.browser.service

import org.reckful.archive.browser.model.Page
import org.reckful.archive.browser.model.browse.*
import org.reckful.archive.browser.model.db.SortOrder
import org.reckful.archive.browser.model.db.VodSortOptions
import org.reckful.archive.browser.repository.VodRepository
import org.reckful.archive.browser.util.withMaxLength
import org.springframework.stereotype.Service

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
        sort: BrowseFilterSort = BrowseFilterSort.DATE_DESC,
        page: Int,
        limit: Int
    ): Page<BrowseVod>

    fun getVodDetailsById(vodId: Long): BrowseVodDetails?
}

@Service
class PersistentBrowseVodService(
    private val browseVodMapperService: BrowseVodMapperService,
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
                displayName = it.chapterName.withMaxLength(40),
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
                year = it.year,
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
        val sortBy = when(sort) {
            BrowseFilterSort.DATE_DESC, BrowseFilterSort.DATE_ASC -> VodSortOptions.DATETIME
            BrowseFilterSort.DURATION_DESC, BrowseFilterSort.DURATION_ASC -> VodSortOptions.DURATION
        }

        val sortOrder = when(sort) {
            BrowseFilterSort.DATE_DESC, BrowseFilterSort.DURATION_DESC -> SortOrder.DESC
            BrowseFilterSort.DATE_ASC, BrowseFilterSort.DURATION_ASC -> SortOrder.ASC
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
        val mirrors = vodRepository.findVodMirrors(vodIds).groupBy { it.vodId }

        val pageVods = vods.map {
            browseVodMapperService.mapToDTO(
                vod = it,
                chapters = chapters[it.id] ?: emptyList(),
                mirrors = mirrors[it.id] ?: emptyList()
            )
        }
        return Page(
            data = pageVods,
            page = page,
            limit = limit
        )
    }

    override fun getVodDetailsById(vodId: Long): BrowseVodDetails? {
        val vod = vodRepository.findById(vodId) ?: return null
        val vodMirrors = vodRepository.findVodMirrors(listOf(vodId))
        return BrowseVodDetails(
            id = vod.id,
            title = vod.title,
            description = vod.description,
            url = browseVodMapperService.getArchiveVideoFileUrl(vodMirrors),
            thumbnailUrl = vod.thumbnailUrl,
            previewSpriteUrl = vod.previewSpriteUrl
        )
    }
}

