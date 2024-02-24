package org.reckful.archive.browser.dto

data class VodDTO(
    val id: Long,
    val title: String,
    val date: String,
    val durationSec: Long,

    val vodThumbnailUrl: String,
    val primaryChapterThumbnailUrl: String,

    val chapters: List<VodChapterDTO>,
)
