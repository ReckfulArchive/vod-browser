package org.reckful.archive.browser.repository

import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.model.db.SortOrder
import org.reckful.archive.browser.model.db.VodCountByChapter
import org.reckful.archive.browser.model.db.VodCountByYear
import org.reckful.archive.browser.model.db.VodSortOptions

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
        likeTitle: String?,
        chapterId: Long?,
        year: Int?,
        sortBy: VodSortOptions,
        sortOrder: SortOrder,
        page: Int,
        limit: Int,
    ): List<VodEntity>

    fun findVodChapters(vodIds: List<Long>): List<VodChapterEntity>

    fun findVodMirrors(vodIds: List<Long>): List<VodMirrorEntity>
}
