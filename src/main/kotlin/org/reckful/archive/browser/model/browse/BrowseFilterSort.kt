package org.reckful.archive.browser.model.browse

import java.util.*
import kotlin.math.abs
import kotlin.random.asKotlinRandom

sealed class BrowseFilterSort(
    val displayName: String,
    val filterValue: String,
    val bootstrapIcon: String
) {
    open val urlValue: String = filterValue

    companion object {
        val DEFAULT_VALUE = DateDescBrowseFilterSort.filterValue
    }
}

data object DateDescBrowseFilterSort : BrowseFilterSort(
    displayName = "Date",
    filterValue = "date_desc",
    bootstrapIcon = "bi-sort-down"
)

data object DateAscBrowseFilterSort : BrowseFilterSort(
    displayName = "Date",
    filterValue = "date_asc",
    bootstrapIcon = "bi-sort-down-alt"
)

data object DurationDescBrowseFilterSort : BrowseFilterSort(
    displayName = "Duration",
    filterValue = "duration_desc",
    bootstrapIcon = "bi-sort-numeric-down-alt"
)

data object DurationAscBrowseFilterSort : BrowseFilterSort(
    displayName = "Duration",
    filterValue = "duration_asc",
    bootstrapIcon = "bi-sort-numeric-down"
)

data class RandomStableBrowseFilterSort(val seed: Int) : BrowseFilterSort(
    displayName = "Random",
    filterValue = createFilterValue(seed),
    bootstrapIcon = "bi-shuffle"
) {
    override val urlValue: String
        get() = URL_VALUE

    companion object {
        private val random = Random().asKotlinRandom()

        const val URL_VALUE = "random"

        fun randomSeed(): Int {
            return abs(random.nextInt(until = 10000))
        }

        fun createFilterValue(seed: Int): String = "$URL_VALUE:$seed"

        fun fromFilterValue(filterValue: String): RandomStableBrowseFilterSort? {
            if (!filterValue.startsWith(URL_VALUE)) {
                return null
            }

            val seed = filterValue.substringAfter("random:").toIntOrNull()
                ?: throw IllegalArgumentException("Seed must be an integer")

            return RandomStableBrowseFilterSort(seed)
        }
    }
}

