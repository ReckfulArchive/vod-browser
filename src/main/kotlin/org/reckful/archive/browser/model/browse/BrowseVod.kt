package org.reckful.archive.browser.model.browse

data class BrowseVod(
    val id: Long,
    val title: String,
    val date: String,
    val duration: String,

    val vodThumbnailUrl: String,
    val primaryChapterThumbnailUrl: String,

    val chapters: List<BrowseVodChapter>,
)
