package org.reckful.archive.browser.model.vod

data class VodDetails(
    val id: Long,
    val externalId: String,
    val title: String,
    val description: String?,
    val dateHuman: String,
    val dateIso: String,
    val url: String?,
    val thumbnailUrl: String,
    val previewSpriteUrl: String?,
)
