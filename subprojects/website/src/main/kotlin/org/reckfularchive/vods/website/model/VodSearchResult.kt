package org.reckfularchive.vods.website.model

data class VodSearchResult(
    val items: List<VodSearchItem>,
    val pageNum: Int,
    val limit: Int
) {
    val hasNextPage get() = items.size >= limit
    val isEmpty get() = items.isEmpty()

    val nextPageQuery = "/search?page=${pageNum + 1}&limit=${limit}"
}
