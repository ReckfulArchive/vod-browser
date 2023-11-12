package org.reckful.archive.browser.model.browse

data class BrowseVodDetails(
    val id: Long,
    val title: String,
    val description: String?,
    val url: String?,
    val thumbnailUrl: String,
    val previewSpriteUrl: String?,
)
