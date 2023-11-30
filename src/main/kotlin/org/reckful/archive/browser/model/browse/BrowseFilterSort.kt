package org.reckful.archive.browser.model.browse

import org.reckful.archive.browser.controller.DEFAULT_SORT_FILTER_VALUE

enum class BrowseFilterSort(
    val displayName: String,
    val filterValue: String,
    val bootstrapIcon: String
) {
    /*
     * BEWARE - the order matters
     */
    DATE_DESC(displayName = "Date", filterValue = DEFAULT_SORT_FILTER_VALUE, bootstrapIcon = "bi-sort-down"),
    DATE_ASC(displayName = "Date", filterValue = "date_asc", bootstrapIcon = "bi-sort-down-alt"),
    DURATION_DESC(displayName = "Duration", filterValue = "duration_desc", bootstrapIcon = "bi-sort-down"),
    DURATION_ASC(displayName = "Duration", filterValue = "duration_asc", bootstrapIcon = "bi-sort-down-alt");

    companion object {
        private val byFilterValue = mapOf(
            *BrowseFilterSort.entries.map { it.filterValue to it }.toTypedArray()
        )

        fun findByFilterValue(filterValue: String): BrowseFilterSort? = byFilterValue[filterValue]
    }
}
