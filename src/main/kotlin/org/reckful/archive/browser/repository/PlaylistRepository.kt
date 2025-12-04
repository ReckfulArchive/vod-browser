package org.reckful.archive.browser.repository

import org.intellij.lang.annotations.Language
import org.reckful.archive.browser.entity.PlaylistEntity
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository


interface PlaylistRepository {
    fun findAll(): List<PlaylistEntity>

    fun findAllByVodId(vodId: Long): List<PlaylistEntity>

    fun save(playlistEntity: PlaylistEntity)

    fun unlinkVodFromPlaylist(vodId: Long, playlistId: Long)

    fun linkVodToPlaylist(vodId: Long, playlistId: Long)
}

@Repository
class JdbcTemplatePlaylistRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : PlaylistRepository {

    override fun findAll(): List<PlaylistEntity> {
        return jdbcTemplate.query(
            "SELECT id, name, description FROM vod_playlist WHERE is_public = true ORDER BY id"
        ) { rs, _ ->
            PlaylistEntity(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
            )
        }
    }

    override fun findAllByVodId(vodId: Long): List<PlaylistEntity> {
        val sql = """
            SELECT id, name, description
            FROM vod_playlist
            WHERE is_public = true
              AND id IN (SELECT playlist_id FROM vod_playlist_items WHERE vod_id = :vodId)
            ORDER BY id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("vodId", vodId)

        return jdbcTemplate.query(sql, params) { rs, _ ->
            PlaylistEntity(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
            )
        }
    }

    override fun save(playlistEntity: PlaylistEntity) {
        @Language("SQL")
        val sql = """
            INSERT INTO vod_playlist(name, description, is_public)
            VALUES (:name, :description, true)
        """.trimIndent()

        val args = MapSqlParameterSource()
            .addValue("name", playlistEntity.name)
            .addValue("description", playlistEntity.description)

        val updatedCount = jdbcTemplate.update(sql, args)
        require(updatedCount == 1) {
            "Failed to create a playlist: $playlistEntity"
        }
    }

    override fun unlinkVodFromPlaylist(vodId: Long, playlistId: Long) {
        val updatedRows = jdbcTemplate.update(
            "DELETE FROM vod_playlist_items WHERE vod_id = :vodId AND playlist_id = :playlistId",
            mapOf("vodId" to vodId, "playlistId" to playlistId)
        )
        require(updatedRows == 1) {
            "Unable to unlink vod $vodId from playlist $playlistId"
        }
    }

    override fun linkVodToPlaylist(vodId: Long, playlistId: Long) {
        val updatedRows = jdbcTemplate.update(
            "INSERT INTO vod_playlist_items(vod_id, playlist_id) VALUES (:vodId, :playlistId)",
            mapOf("vodId" to vodId, "playlistId" to playlistId)
        )
        require(updatedRows == 1) {
            "Unable to link vod $vodId to playlist $playlistId"
        }
    }
}
