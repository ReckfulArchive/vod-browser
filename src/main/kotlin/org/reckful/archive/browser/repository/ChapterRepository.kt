package org.reckful.archive.browser.repository

import org.reckful.archive.browser.model.VodChapter
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

interface ChapterRepository {
    fun findVodChapters(vodId: Long): List<VodChapter>

    fun saveVodChapter(vodId: Long, chapterId: Long, startTimeSec: Int)

    fun findPopularChapters(): List<VodChapter>


    fun removeVodChapter(vodId: Long, chapterId: Long, startTimeSec: Int)
}

@Repository
class JdbcTemplateChapterRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : ChapterRepository {

    override fun findVodChapters(vodId: Long): List<VodChapter> {
        val sql = """
            SELECT vod_chapter.id, vod_chapter.name, vc.vod_id, vc.start_time
            FROM vod_chapter
                     JOIN public.vod_chapters vc on vod_chapter.id = vc.chapter_id
            WHERE vod_id = :vodId
            ORDER BY start_time
        """.trimIndent()
        return jdbcTemplate.query(sql, mapOf("vodId" to vodId)) { rs, _ ->
            VodChapter(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                vodId = rs.getLong("vod_id"),
                startTimeSec = rs.getInt("start_time")
            )
        }
    }

    override fun saveVodChapter(vodId: Long, chapterId: Long, startTimeSec: Int) {
        val updatedRows = jdbcTemplate.update(
            "INSERT INTO vod_chapters(vod_id, chapter_id, start_time) VALUES (:vodId, :chapterId, :startTimeSec)",
            mapOf("vodId" to vodId, "chapterId" to chapterId, "startTimeSec" to startTimeSec)
        )
        require(updatedRows == 1) {
            "Unable to save vod chapter $chapterId for vod $vodId with time $startTimeSec"
        }
    }

    override fun removeVodChapter(vodId: Long, chapterId: Long, startTimeSec: Int) {
        val updatedRows = jdbcTemplate.update(
            "DELETE FROM vod_chapters WHERE vod_id = :vodId AND chapter_id = :chapterId AND start_time = :startTimeSec",
            mapOf("vodId" to vodId, "chapterId" to chapterId, "startTimeSec" to startTimeSec)
        )
        require(updatedRows == 1) {
            "Unable to save vod chapter $chapterId for vod $vodId with time $startTimeSec"
        }
    }

    override fun findPopularChapters(): List<VodChapter> {
        return jdbcTemplate.query(
            "SELECT id, name FROM vod_chapter WHERE popularity >= 999999 ORDER BY popularity DESC, id"
        ) { rs, _ ->
            VodChapter(
                id = rs.getLong("id"),
                name = rs.getString("name")
            )
        }
    }
}
