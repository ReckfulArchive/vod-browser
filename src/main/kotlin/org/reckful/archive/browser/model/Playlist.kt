package org.reckful.archive.browser.model

data class Playlist(
    val id: Long,

    val name: String,
    val description: String,
    val thumbnailUrl: String,

    val vodCount: Int,
)
