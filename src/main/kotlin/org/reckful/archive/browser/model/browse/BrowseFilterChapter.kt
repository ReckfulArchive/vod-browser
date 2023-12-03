package org.reckful.archive.browser.model.browse

data class BrowseFilterChapter(
    val chapterId: Long,
    val chapterName: String,
    val vodCount: Int,
) {
    companion object {
        const val DEFAULT_VALUE_TEXT = "All"
        const val DEFAULT_VALUE = "all"
    }
}
