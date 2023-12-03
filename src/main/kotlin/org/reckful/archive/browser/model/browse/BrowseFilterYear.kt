package org.reckful.archive.browser.model.browse

data class BrowseFilterYear(
    val yearValue: Int,
    val vodCount: Int
) {
    companion object {
        const val DEFAULT_VALUE_TEXT = "All"
        const val DEFAULT_VALUE = "all"
    }
}
