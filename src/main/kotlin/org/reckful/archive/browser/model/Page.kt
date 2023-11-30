package org.reckful.archive.browser.model

data class Page<T>(
    val data: List<T>,
    val pageNum: Int,
    val limit: Int
) {
    val hasNext get() = data.size >= limit
    val isEmpty get() = data.isEmpty()
}
