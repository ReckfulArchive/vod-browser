package org.reckful.archive.browser.model.vod

data class VodSuggestion(
    val vodId: Long,
    val thumbnailUrl: String,
    val title: String,
    val date: String,
    val duration: String
)
