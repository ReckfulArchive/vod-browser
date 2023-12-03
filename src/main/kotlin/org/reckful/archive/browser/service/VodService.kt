package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.model.vod.VodDetails
import org.reckful.archive.browser.repository.VodRepository
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.time.format.DateTimeFormatter
import java.util.*

@Service
interface VodService {
    fun getVodDetailsById(vodId: Long): VodDetails?

    fun saveReport(vodId: Long, type: String, message: String)
}

@Service
class PersistentVodService(
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

    override fun saveReport(vodId: Long, type: String, message: String) {
        vodRepository.insertReport(vodId, type, message)
    }

    private companion object {
        private val VOD_DETAILS_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
    }
}
