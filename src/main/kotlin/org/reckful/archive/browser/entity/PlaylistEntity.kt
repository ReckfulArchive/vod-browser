package org.reckful.archive.browser.entity

data class PlaylistEntity(
    val id: Long? = null,
    val name: String,
    val description: String,
) {
    val idNotNull get() = requireNotNull(id)
}
