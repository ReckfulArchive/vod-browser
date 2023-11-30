package org.reckful.archive.browser.model.db

enum class SortOrder {
    ASC, DESC;

    fun asSql(): String = this.name
}
