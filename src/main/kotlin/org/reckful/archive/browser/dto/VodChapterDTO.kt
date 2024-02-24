package org.reckful.archive.browser.dto

data class VodChapterDTO(
    val name: String,
    val thumbnailUrl: String,
    val startTimeSec: Long,
    val durationSec: Long,
)
