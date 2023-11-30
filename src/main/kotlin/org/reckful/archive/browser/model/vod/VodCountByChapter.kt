package org.reckful.archive.browser.model.vod

data class VodCountByChapter(
    val chapterId: Long,
    val chapterName: String,
    val vodCount: Int
)
