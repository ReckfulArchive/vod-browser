package org.reckful.archive.browser.dto

data class EditVodForm(
    val id: Long,
    val title: String,
    val description: String?,
    val dateIso: String,
    val playlists: List<Long>
)
