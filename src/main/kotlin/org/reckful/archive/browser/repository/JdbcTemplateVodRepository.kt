@file:Suppress("SqlSourceToSinkFlow")

package org.reckful.archive.browser.repository

import org.intellij.lang.annotations.Language
import org.reckful.archive.browser.entity.VodChapterEntity
import org.reckful.archive.browser.entity.VodEntity
import org.reckful.archive.browser.entity.VodMirrorEntity
import org.reckful.archive.browser.model.db.SortOrder
import org.reckful.archive.browser.model.vod.*
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

// TODO clean up copy-paste
@Repository
class JdbcTemplateVodRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : VodRepository {
    override fun findById(vodId: Long): VodEntity? {
        @Language("SQL")
        val sql = """
            SELECT vod.id, vod.external_id, vod.title, vod.thumbnail_url, vod.upload_ts, 
                   vod.duration_sec, vod.description, vod.preview_sprite_url
            FROM vod
            WHERE vod.id = :vodId
        """.trimIndent()

        return jdbcTemplate.queryForObject(sql, mapOf("vodId" to vodId)) { rs, _ ->
            VodEntity(
                id = rs.getLong(1),
                externalId = rs.getString(2),
                title = rs.getString(3),
                thumbnailUrl = rs.getString(4),
                dateTime = rs.getTimestamp(5).toLocalDateTime(),
                duration = Duration.ofSeconds(rs.getLong(6)),
                description = rs.getString(7),
                previewSpriteUrl = rs.getString(8),
            )
        }
    }

    override fun countChapterVods(playlistIds: List<Long>, likeTitle: String?, year: Int?): List<VodCountByChapter> {
        val sql = buildString(400) {
            appendLine("SELECT vod_chapter.id, vod_chapter.name, count(distinct vod_chapters.vod_id) as vod_count")
            appendLine("FROM vod_chapter")
            appendLine("JOIN vod_chapters on vod_chapter.id = vod_chapters.chapter_id")
            appendLine("WHERE vod_chapters.vod_id IN (SELECT vod.id FROM vod")
            if (playlistIds.isNotEmpty()) {
                appendLine("INNER JOIN vod_playlist_items ON vod_playlist_items.vod_id = vod.id")
            }
            appendLine("WHERE vod.is_public = true")
            if (year != null) {
                appendLine("AND EXTRACT(YEAR FROM vod.upload_ts) = :year")
            }
            if (playlistIds.isNotEmpty()) {
                appendLine("AND vod_playlist_items.playlist_id IN (:playlistIds)")
            }
            if (likeTitle != null) {
                appendLine("AND vod.title ILIKE :likeTitle")
            }
            append(")")
            appendLine("GROUP BY vod_chapter.id, vod_chapter.name")
            appendLine("ORDER BY vod_count DESC, vod_chapter.name")
        }

        val params = MapSqlParameterSource()
            .addValues("playlistIds", playlistIds)
            .addIfNotNull("year", year)
            .addIfNotNull("likeTitle", likeTitle?.let { "%$it%" })

        return jdbcTemplate.query(sql, params) { rs, _ ->
            VodCountByChapter(
                chapterId = rs.getLong(1),
                chapterName = rs.getString(2),
                vodCount = rs.getInt(3)
            )
        }
    }

    override fun countYearVods(
        playlistIds: List<Long>,
        likeTitle: String?,
        chapterId: Long?
    ): List<VodCountByYear> {
        val sql = buildString {
            appendLine("SELECT EXTRACT(YEAR FROM vod.upload_ts) as year, count(DISTINCT vod.id) as vod_count")
            appendLine("FROM vod")
            if (playlistIds.isNotEmpty()) {
                appendLine("INNER JOIN vod_playlist_items ON vod_playlist_items.vod_id = vod.id")
            }
            if (chapterId != null) {
                appendLine("LEFT JOIN vod_chapters ON vod_chapters.vod_id = vod.id")
                appendLine("LEFT JOIN vod_chapter ON vod_chapters.chapter_id = vod_chapter.id")
            }
            appendLine("WHERE vod.is_public = true")
            if (playlistIds.isNotEmpty()) {
                appendLine("AND vod_playlist_items.playlist_id IN (:playlistIds)")
            }
            if (likeTitle != null) {
                appendLine("AND vod.title ILIKE :likeTitle")
            }
            if (chapterId != null) {
                appendLine("AND vod_chapters.chapter_id = :chapterId")
            }
            appendLine("GROUP BY EXTRACT(YEAR FROM vod.upload_ts)")
            appendLine("ORDER BY year DESC")
        }

        val params = MapSqlParameterSource()
            .addValues("playlistIds", playlistIds)
            .addIfNotNull("chapterId", chapterId)
            .addIfNotNull("likeTitle", likeTitle?.let { "%$it%" })

        return jdbcTemplate.query(sql, params) { rs, _ ->
            VodCountByYear(
                year = rs.getInt(1),
                vodCount = rs.getInt(2)
            )
        }
    }

    /**
     * @param page 1-based
     */
    override fun findVods(
        playlistIds: List<Long>,
        likeTitle: String?,
        chapterId: Long?,
        year: Int?,
        sortBy: VodSortOption,
        sortOrder: SortOrder,
        page: Int,
        limit: Int,
    ): List<VodEntity> {
        val sortValue = sortBy.value()
        val sql = buildString(450) {
            appendLine(
                "SELECT DISTINCT vod.id, vod.external_id, vod.title, vod.thumbnail_url, " +
                        "vod.upload_ts, vod.duration_sec, vod.description, vod.preview_sprite_url, $sortValue"
            )
            appendLine("FROM vod")
            if (playlistIds.isNotEmpty()) {
                appendLine("INNER JOIN vod_playlist_items ON vod_playlist_items.vod_id = vod.id")
            }
            if (chapterId != null) {
                appendLine("LEFT JOIN vod_chapters ON vod_chapters.vod_id = vod.id")
                appendLine("LEFT JOIN vod_chapter ON vod_chapters.chapter_id = vod_chapter.id")
            }
            appendLine("WHERE vod.is_public = true")
            if (year != null) {
                appendLine("AND EXTRACT(YEAR FROM vod.upload_ts) = :year")
            }
            if (playlistIds.isNotEmpty()) {
                appendLine("AND vod_playlist_items.playlist_id IN (:playlistIds)")
            }
            if (likeTitle != null) {
                appendLine("AND vod.title ILIKE :likeTitle")
            }
            if (chapterId != null) {
                appendLine("AND vod_chapters.chapter_id = :chapterId")
            }
            appendLine("ORDER BY $sortValue ${sortOrder.asSql()}, id")
            appendLine("LIMIT :limit OFFSET :offset")
        }

        val params = MapSqlParameterSource()
            .addIfNotNull("year", year)
            .addValues("playlistIds", playlistIds)
            .addIfNotNull("likeTitle", likeTitle?.let { "%$it%" })
            .addIfNotNull("chapterId", chapterId)
            .addValue("limit", limit)
            .addValue("offset", (page - 1) * limit)

        return jdbcTemplate.query(sql, params, ROW_MAPPER_VOD_ENTITY)
    }

    private fun VodSortOption.value(): String {
        return when (this) {
            DateTimeVodSortOption -> "vod.upload_ts"
            DurationVodSortOption -> "vod.duration_sec"
            is RandomStableVodSortOption -> "hashint8(vod.id + ${this.seed})"
        }
    }

    override fun findVodChapters(vodIds: List<Long>): List<VodChapterEntity> {
        if (vodIds.isEmpty()) return emptyList()

        @Language("SQL")
        val sql = """
            select vod_chapters.vod_id, vod_chapter.id, vod_chapter.name, 
                   vod_chapters.start_time, vod_chapter.thumbnail_url
            from vod_chapters
                     join vod_chapter on vod_chapters.chapter_id = vod_chapter.id
            where vod_chapters.vod_id in (:vodIds);
        """.trimIndent()

        val params = MapSqlParameterSource().addValues("vodIds", vodIds)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            VodChapterEntity(
                vodId = rs.getLong(1),
                chapterId = rs.getLong(2),
                name = rs.getString(3),
                startTimeSec = rs.getLong(4),
                thumbnailUrl = rs.getString(5)
            )
        }
    }

    override fun findVodMirrors(vodIds: List<Long>): List<VodMirrorEntity> {
        if (vodIds.isEmpty()) return emptyList()

        @Language("SQL")
        val sql = "select vod_id, type, url from vod_mirrors where vod_id IN (:vodIds)"

        val params = MapSqlParameterSource().addValues("vodIds", vodIds)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            VodMirrorEntity(
                vodId = rs.getLong(1),
                type = rs.getString(2),
                url = rs.getString(3),
            )
        }
    }

    private fun MapSqlParameterSource.addIfNotNull(key: String, value: Any?): MapSqlParameterSource {
        if (value != null) {
            this.addValue(key, value)
        }
        return this
    }

    private inline fun <reified T> MapSqlParameterSource.addValues(
        name: String,
        values: List<T>
    ): MapSqlParameterSource {
        if (values.isNotEmpty()) {
            this.addValue(name, values)
        }
        return this
    }

    override fun insertReport(vodId: Long, type: String, message: String) {
        @Language("SQL")
        val sql = "INSERT INTO vod_reports(vod_id, type, message) VALUES (:vod_id, :type, :message);"

        jdbcTemplate.update(
            sql, mapOf(
                "vod_id" to vodId,
                "type" to type,
                "message" to message
            )
        )
    }

    override fun findNextByDate(vodId: Long, playlistIds: List<Long>, page: Int, limit: Int): List<VodEntity> {
        val sql = buildString {
            appendLine("WITH selected_vod AS (SELECT upload_ts FROM vod WHERE id = :vod_id)")
            appendLine(
                "SELECT DISTINCT vod.id, vod.external_id, vod.title, vod.thumbnail_url, " +
                        "vod.upload_ts, vod.duration_sec, vod.description, vod.preview_sprite_url"
            )
            appendLine("FROM vod")
            if (playlistIds.isNotEmpty()) {
                appendLine("INNER JOIN vod_playlist_items ON vod_playlist_items.vod_id = vod.id")
            }
            appendLine("WHERE upload_ts > (SELECT upload_ts FROM selected_vod)")
            if (playlistIds.isNotEmpty()) {
                appendLine("AND vod_playlist_items.playlist_id IN (:playlistIds)")
            }
            appendLine("ORDER BY upload_ts, id desc")
            appendLine("LIMIT :limit OFFSET :offset")
        }

        val params = MapSqlParameterSource()
            .addValue("vod_id", vodId)
            .addValues("playlistIds", playlistIds)
            .addValue("limit", limit)
            .addValue("offset", (page - 1) * limit)

        return jdbcTemplate.query(sql, params, ROW_MAPPER_VOD_ENTITY)
    }

    private companion object {
        val ROW_MAPPER_VOD_ENTITY =
            RowMapper<VodEntity> { rs, rowNum ->
                VodEntity(
                    id = rs.getLong("id"),
                    externalId = rs.getString("external_id"),
                    title = rs.getString("title"),
                    thumbnailUrl = rs.getString("thumbnail_url"),
                    dateTime = rs.getTimestamp("upload_ts").toLocalDateTime(),
                    duration = Duration.ofSeconds(rs.getLong("duration_sec")),
                    description = rs.getString("description"),
                    previewSpriteUrl = rs.getString("preview_sprite_url"),
                )
            }
    }
}
