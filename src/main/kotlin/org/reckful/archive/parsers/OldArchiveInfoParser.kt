package org.reckful.archive.parsers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.IllegalArgumentException

private const val OLD_ARCHIVE_INFO_DIR = "/home/ignat/IdeaProjects/twitch-metadata/src/main/resources/old-archive-info"

class OldArchiveInfoParser {

    fun getOldArchiveInfo(): List<OldArchiveTwitchVodInfo> {
        val archiveInfoDir = File(OLD_ARCHIVE_INFO_DIR).takeIf { it.exists() }
            ?: throw IllegalArgumentException("Expected the old archive info dir to exist")

        val infoFiles = archiveInfoDir.listFiles() ?: throw IllegalStateException("No files for dir $archiveInfoDir")
        return infoFiles
            .map {
                try {
                    val jsonContent = it.readText()
                    Json.decodeFromString(jsonContent)
                } catch (e: Exception) {
                    throw IllegalStateException("Unable to parse file $it", e)
                }
            }
    }
}

@Serializable
data class OldArchiveTwitchVodInfo(
    val extractor: String,
    val protocol: String,
    @SerialName("format_note")
    val formatNote: String? = null,
    @SerialName("upload_date")
    val uploadDate: String,
    val height: Int? = null,
    val duration: Int,
    @SerialName("manifest_url")
    val manifestUrl: String,
    val fulltitle: String,
    val quality: Int? = null,
    val id: String,
    @SerialName("view_count")
    val viewCount: Int,
    val playlist: String?, // `Any?` represents a nullable variable of any type
    val title: String,
    @SerialName("_filename")
    val filename: String,
    @SerialName("is_live")
    val isLive: Boolean,
    @SerialName("playlist_index")
    val playlistIndex: Int?,
    val width: Int? = null,
    val fps: Double?,
    val thumbnail: String,
    @SerialName("webpage_url_basename")
    val webpageUrlBasename: String,
    val acodec: String? = null,
    @SerialName("display_id")
    val displayId: String,
    val description: String? = null,
    val format: String,
    val timestamp: Long,
    val tbr: Double,
    val preference: Int?,
    val uploader: String,
    @SerialName("format_id")
    val formatId: String,
    @SerialName("uploader_id")
    val uploaderId: String,
    val subtitles: Subtitles,
    val thumbnails: List<Thumbnail>,
    val url: String,
    @SerialName("extractor_key")
    val extractorKey: String,
    val vcodec: String? = null,
    @SerialName("http_headers")
    val httpHeaders: HttpHeaders,
    val ext: String,
    @SerialName("webpage_url")
    val webpageUrl: String,
    val formats: List<Format>
)

@Serializable
data class Subtitles(
    val rechat: List<Rechat>
)

@Serializable
data class Rechat(
    val url: String,
    val ext: String
)

@Serializable
data class Thumbnail(
    val url: String,
    val preference: Int,
    val id: String
)

@Serializable
data class HttpHeaders(
    @SerialName("Accept-Charset")
    val acceptCharset: String,

    @SerialName("Accept-Language")
    val acceptLanguage: String,

    @SerialName("Accept-Encoding")
    val acceptEncoding: String,

    @SerialName("Accept")
    val accept: String,

    @SerialName("User-Agent")
    val userAgent: String
)

@Serializable
data class Format(
    @SerialName("http_headers")
    val httpHeaders: HttpHeaders? = null,
    val protocol: String,
    val format: String,
    @SerialName("format_note")
    val formatNote: String? = null,
    val url: String? = null,
    val vcodec: String? = null,
    val tbr: Double? = null,
    val height: Int? = null,
    val width: Int? = null,
    val ext: String? = null,
    val preference: String? = null,
    val fps: Double? = null,
    @SerialName("manifest_url")
    val manifestUrl: String? = null,
    @SerialName("format_id")
    val formatId: String? = null,
    val quality: Int? = null,
    val acodec: String? = null
)
