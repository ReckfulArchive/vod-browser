package org.reckful.archive.browser.entity

data class UserEntity(
    val id: Long,
    val name: String,
    val passwordEncoded: String,
)
