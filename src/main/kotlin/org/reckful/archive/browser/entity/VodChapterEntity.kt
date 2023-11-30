package org.reckful.archive.browser.entity

data class VodChapterEntity(
    val vodId: Long,
    val chapterId: Long,
    val name: String,
    val startTimeSec: Long,
    val thumbnailUrl: String,
)
