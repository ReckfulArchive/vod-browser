package org.reckful.archive.browser.repository

import org.intellij.lang.annotations.Language
import org.reckful.archive.browser.entity.PlaylistEntity
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository


interface PlaylistRepository {
    fun findAll(): List<PlaylistEntity>

    fun save(playlistEntity: PlaylistEntity)
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
}
