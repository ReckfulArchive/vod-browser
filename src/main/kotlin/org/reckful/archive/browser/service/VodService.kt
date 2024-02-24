package org.reckful.archive.browser.service

import org.reckful.archive.browser.dto.VodChapterDTO
import org.reckful.archive.browser.dto.VodDTO
import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.mapper.ChapterMapper
import org.reckful.archive.browser.model.vod.VodDetails
import org.reckful.archive.browser.repository.VodRepository
import org.reckful.archive.browser.service.ChapterService.Companion.DEFAULT_CHAPTER_THUMBNAIL_URL
import org.reckful.archive.browser.util.findWithLongestDurationGroupedBy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.time.format.DateTimeFormatter
import java.util.*

@Service
interface VodService {
    fun getVodDetailsById(vodId: Long): VodDetails?

    fun findArchiveVodsByFileName(archiveFileName: String): List<VodDTO>

    fun saveReport(vodId: Long, type: String, message: String)
}

@Service
class PersistentVodService(
    private val chapterMapper: ChapterMapper,
    private val vodRepository: VodRepository
) : VodService {

    override fun getVodDetailsById(vodId: Long): VodDetails? {
        val vod = vodRepository.findById(vodId) ?: return null
        val vodMirrors = vodRepository.findVodMirrors(listOf(vodId))
        return VodDetails(
            id = vod.id,
            externalId = vod.externalId,
            title = vod.title,
            description = vod.description,
            date = vod.dateTime.format(VOD_DETAILS_DATE_FORMATTER),
            url = getArchiveVideoFileUrl(vodMirrors),
            thumbnailUrl = vod.thumbnailUrl,
            previewSpriteUrl = vod.previewSpriteUrl
        )
    }

    private fun getArchiveVideoFileUrl(mirrors: List<VodMirrorEntity>): String? {
        val archiveMirror = mirrors.firstOrNull { it.type == "ARCHIVE_FILE_PATH" } ?: return null
        val archivePath = URLEncoder.encode(archiveMirror.url, "UTF-8").replace("+", "%20")
        return "https://files.reckful-archive.org/$archivePath"
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    override fun findArchiveVodsByFileName(archiveFileName: String): List<VodDTO> {
        val vods = vodRepository.findByArchiveFileName(archiveFileName)
        if (vods.isEmpty()) return emptyList()

        val chapters = vodRepository.findVodChapters(vods.map { it.id }).groupBy { it.vodId }
        return vods.map {
            it.toDTO(chapters = chapters[it.id] ?: emptyList())
        }
    }

    private fun VodEntity.toDTO(chapters: List<VodChapterEntity>): VodDTO {
        val chaptersWithDuration = chapterMapper.withDuration(
            chapters = chapters,
            vodDuration = this.duration
        ) { chapter, duration ->
            VodChapterDTO(
                name = chapter.name,
                thumbnailUrl = chapter.thumbnailUrl,
                startTimeSec = chapter.startTimeSec,
                durationSec = duration.toSeconds()
            )
        }

        val primaryChapterThumbnailUrl = chaptersWithDuration
            .findWithLongestDurationGroupedBy { it.name }
            ?.thumbnailUrl
            ?: DEFAULT_CHAPTER_THUMBNAIL_URL

        return VodDTO(
            id = this.id,
            title = this.title,
            date = this.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            durationSec = this.duration.toSeconds(),
            vodThumbnailUrl = this.thumbnailUrl,
            primaryChapterThumbnailUrl = primaryChapterThumbnailUrl,
            chapters = chaptersWithDuration.map { it.data }
        )
    }

    override fun saveReport(vodId: Long, type: String, message: String) {
        vodRepository.insertReport(vodId, type, message)
    }

    private companion object {
        private val VOD_DETAILS_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
    }
}
