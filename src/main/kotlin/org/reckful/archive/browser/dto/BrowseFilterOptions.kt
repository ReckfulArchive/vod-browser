package org.reckful.archive.browser.dto

import org.reckful.archive.browser.model.browse.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class BrowseFilterOptions(
    val playlist: Long?,
    val title: String?,
    val chapter: String?,
    val year: String?,
    val sort: String?
) {
    fun parseTitle(): String? = title?.trim()?.takeIf { it.isNotBlank() }

    fun parseChapterId(): Long? = chapter?.toLongOrNull()

    fun parseYear(): Int? = year?.toIntOrNull()

    fun parseSort(): BrowseFilterSort {
        val sortValue = sort ?: BrowseFilterSort.DEFAULT_VALUE
        return requireNotNull(findByFilterValue(sortValue)) {
            "Param \"sort\" could not be mapped."
        }
    }

    fun hasRandomSortWithoutSeed(): Boolean {
        return sort == RandomStableBrowseFilterSort.URL_VALUE
    }

    fun buildQueryParams(): String {
        return buildQueryParams {
            add("title" to title)
            add("chapter" to chapter.takeIf { it != BrowseFilterChapter.DEFAULT_VALUE })
            add("year" to year.takeIf { it != BrowseFilterYear.DEFAULT_VALUE })
            add("sort" to sort
                .takeIf { it != BrowseFilterSort.DEFAULT_VALUE }
                ?.let { findByFilterValue(it) }
                ?.urlValue
            )
        }
    }

    private fun findByFilterValue(filterValue: String): BrowseFilterSort? = when {
        DateDescBrowseFilterSort.filterValue == filterValue -> DateDescBrowseFilterSort
        DateAscBrowseFilterSort.filterValue == filterValue -> DateAscBrowseFilterSort
        DurationDescBrowseFilterSort.filterValue == filterValue -> DurationDescBrowseFilterSort
        DurationAscBrowseFilterSort.filterValue == filterValue -> DurationAscBrowseFilterSort
        filterValue.startsWith(RandomStableBrowseFilterSort.URL_VALUE) -> {
            RandomStableBrowseFilterSort.fromFilterValue(filterValue)
        }
        else -> null
    }

    private fun buildQueryParams(block: MutableList<Pair<String, String?>>.() -> Unit): String {
        val params = mutableListOf<Pair<String, String?>>()
        block(params)
        return params
            .filter { it.second?.isNotBlank() == true }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = "?", separator = "&") { (name, value) ->
                val valueEncoded = URLEncoder.encode(value, StandardCharsets.UTF_8)
                "${name}=${valueEncoded}"
            } ?: ""
    }
}
