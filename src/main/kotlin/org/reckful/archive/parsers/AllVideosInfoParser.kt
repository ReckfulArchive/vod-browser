@file:Suppress("DuplicatedCode")

package org.reckful.archive.parsers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.IllegalArgumentException

private const val ALL_VIDEOS_INFO_DIR_PATH = "all-videos-info"

class AllVideosInfoParser(
    private val filesDirectory: File
) {
    fun getVideosInfo(): List<VideoInfo> {
        val infoDir = filesDirectory.resolve(ALL_VIDEOS_INFO_DIR_PATH).takeIf { it.exists() }
            ?: throw IllegalArgumentException("Expected the info dir to exist")

        val infoFiles = infoDir.listFiles() ?: throw IllegalStateException("No files for dir $infoDir")
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
data class VideoInfo(
    val id: String,
    val title: String,
    val description: String? = null,
    val duration: Int? = null,
    val thumbnail: String,
    val uploader: String,
    @SerialName("uploader_id")
    val uploaderId: String,
    val timestamp: Long,
    @SerialName("view_count")
    val viewCount: Int,
    val chapters: List<Chapter>,
    @SerialName("is_live")
    val isLive: Boolean,
    @SerialName("was_live")
    val wasLive: Boolean,
    val formats: List<Format>,
    val subtitles: Subtitles? = null,
    @SerialName("webpage_url")
    val webpageUrl: String,
    @SerialName("webpage_url_basename")
    val webpageUrlBasename: String? = null,
    @SerialName("webpage_url_domain")
    val webpageUrlDomain: String? = null,
    val extractor: String? = null,
    @SerialName("extractor_key")
    val extractorKey: String? = null,
    val thumbnails: List<Thumbnail>? = null,
    @SerialName("display_id")
    val displayId: String? = null,
    @SerialName("fulltitle")
    val fullTitle: String,
    @SerialName("duration_string")
    val durationString: String? = null,
    @SerialName("upload_date")
    val uploadDate: String? = null,
    @SerialName("live_status")
    val liveStatus: String? = null,
    @SerialName("format_id")
    val formatId: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("manifest_url")
    val manifestUrl: String? = null,
    val ext: String? = null,
    val protocol: String? = null,
    val quality: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val acodec: String? = null,
    val vcodec: String? = null,
    @SerialName("dynamic_range")
    val dynamicRange: String? = null,
    val resolution: String? = null,
    @SerialName("filesize_approx")
    val filesizeApprox: Long? = null,
    @SerialName("aspect_ratio")
    val aspectRatio: Double? = null,
    @SerialName("http_headers")
    val httpHeaders: HttpHeaders,
    @SerialName("audio_ext")
    val audioExt: String,
    @SerialName("video_ext")
    val videoExt: String,
    val format: String,
    val tbr: Double? = null,
    val abr: Double? = null,
    val vbr: Double? = null,
    val fps: Double? = null,
    @SerialName("format_note")
    val formatNote: String? = null,
    val epoch: Long,
    @SerialName("_type")
    val type: String,
    @SerialName("_version")
    val version: Version
)

@Serializable
data class Chapter(
    val title: String,
    @SerialName("start_time")
    val startTime: Int,
    @SerialName("end_time")
    val endTime: Int? = null
)

@Serializable
data class Format(
    @SerialName("format_id")
    val formatId: String,
    @SerialName("format_note")
    val formatNote: String? = null,
    val ext: String,
    val protocol: String,
    val acodec: String? = null,
    val vcodec: String? = null,
    val url: String,
    @SerialName("manifest_url")
    val manifestUrl: String? = null,
    val tbr: Double? = null,
    val abr: Double? = null,
    val vbr: Double? = null,
    val width: Int? = null,
    val height: Int? = null,
    val fps: Double? = null,
    val rows: Int? = null,
    val columns: Int? = null,
    val fragments: List<Fragment>? = null,
    val quality: Int? = null,
    @SerialName("dynamic_range")
    val dynamicRange: String? = null,
    val resolution: String? = null,
    @SerialName("filesize_approx")
    val filesizeApprox: Long? = null,
    @SerialName("aspect_ratio")
    val aspectRatio: Double? = null,
    @SerialName("http_headers")
    val httpHeaders: HttpHeaders,
    @SerialName("audio_ext")
    val audioExt: String,
    @SerialName("video_ext")
    val videoExt: String,
    val format: String
)

@Serializable
data class Fragment(
    val url: String,
    val duration: Double
)

@Serializable
data class HttpHeaders(
    @SerialName("User-Agent")
    val userAgent: String,
    val Accept: String,
    @SerialName("Accept-Language")
    val acceptLanguage: String,
    @SerialName("Sec-Fetch-Mode")
    val secFetchMode: String
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
    val preference: Int? = null,
    val id: String,
)

@Serializable
data class Version(
    val version: String,
    @SerialName("release_git_head")
    val releaseGitHead: String,
    val repository: String
)
