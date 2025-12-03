package org.reckful.archive.browser.repository

import org.reckful.archive.browser.entity.UserEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class JdbcClientUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : UserRepository {

    override fun findByName(name: String): UserEntity? {
        return jdbcTemplate.query(
            "SELECT id, name, password_encoded FROM user_details WHERE lower(name) = lower(:name)",
            mapOf("name" to name)
        ) { rs, _ ->
            UserEntity(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                passwordEncoded = rs.getString("password_encoded"),
            )
        }.singleOrNull()
    }

    override fun loadAuthorities(userId: Long): List<String> {
        return jdbcTemplate.query(
            "SELECT authority FROM user_authorities WHERE user_id = :user_id",
            mapOf("user_id" to userId)
        ) { rs, _ ->
            rs.getString("authority")
        }
    }
}
