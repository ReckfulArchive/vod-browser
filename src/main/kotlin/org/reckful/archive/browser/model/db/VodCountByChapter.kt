package org.reckful.archive.browser.model.db

data class VodCountByChapter(
    val chapterId: Long,
    val chapterName: String,
    val vodCount: Int
)
