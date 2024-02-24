package org.reckful.archive.browser.repository

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.model.db.SortOrder
import org.reckful.archive.browser.model.vod.VodCountByChapter
import org.reckful.archive.browser.model.vod.VodCountByYear
import org.reckful.archive.browser.model.vod.VodSortOption

interface VodRepository {

    fun findById(vodId: Long): VodEntity?

    fun countChapterVods(
        playlistIds: List<Long>,
        likeTitle: String?,
        year: Int?
    ): List<VodCountByChapter>

    fun countYearVods(
        playlistIds: List<Long>,
        likeTitle: String?,
        chapterId: Long?
    ): List<VodCountByYear>

    fun findVods(
        playlistIds: List<Long>,
        likeTitle: String? = null,
        chapterId: Long? = null,
        year: Int? = null,
        sortBy: VodSortOption,
        sortOrder: SortOrder = SortOrder.ASC,
        page: Int,
        limit: Int,
    ): List<VodEntity>

    fun findVodChapters(vodIds: List<Long>): List<VodChapterEntity>

    fun findVodMirrors(vodIds: List<Long>): List<VodMirrorEntity>

    fun insertReport(vodId: Long, type: String, message: String)

    fun findNextByDate(
        vodId: Long,
        playlistIds: List<Long>,
        page: Int, limit: Int
    ): List<VodEntity>

    fun findByArchiveFileName(fileName: String): List<VodEntity>
}
