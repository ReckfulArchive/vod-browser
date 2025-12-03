package org.reckful.archive.browser.repository

import org.reckful.archive.browser.entity.UserEntity

interface UserRepository {
    fun findByName(name: String): UserEntity?

    fun loadAuthorities(userId: Long): List<String>
}
