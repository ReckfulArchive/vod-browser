package org.reckful.archive.browser.dto

import org.reckful.archive.browser.controller.DEFAULT_CHAPTER_FILTER_VALUE
import org.reckful.archive.browser.controller.DEFAULT_SORT_FILTER_VALUE
import org.reckful.archive.browser.controller.DEFAULT_YEAR_FILTER_VALUE
import org.reckful.archive.browser.model.browse.BrowseFilterSort

data class BrowseFilterOptions(
    val title: String?,
    val chapter: String?,
    val year: String?,
    val sort: String?
) {
    fun parseTitle(): String? = title?.trim()?.takeIf { it.isNotBlank() }

    fun parseChapterId(): Long? = chapter?.toLongOrNull()

    fun parseYear(): Int? = year?.toIntOrNull()

    fun parseSort(): BrowseFilterSort {
        val sortValue = sort ?: DEFAULT_SORT_FILTER_VALUE
        return requireNotNull(BrowseFilterSort.findByFilterValue(sortValue)) {
            "Param \"sort\" could not be mapped. Expected values: " +
                    BrowseFilterSort.entries.joinToString(separator = ", ") { it.filterValue }
        }
    }

    fun buildQueryParams(): String {
        return buildQueryParams {
            add("title" to title)
            add("chapter" to chapter.takeIf { it != DEFAULT_CHAPTER_FILTER_VALUE })
            add("year" to year.takeIf { it != DEFAULT_YEAR_FILTER_VALUE })
            add("sort" to sort.takeIf { it != DEFAULT_SORT_FILTER_VALUE })
        }
    }

    private fun buildQueryParams(block: MutableList<Pair<String, String?>>.() -> Unit): String {
        val params = mutableListOf<Pair<String, String?>>()
        block(params)
        return params
            .filter { it.second?.isNotBlank() == true }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = "?", separator = "&") { "${it.first}=${it.second}" }
            ?: ""
    }
}
