package org.reckful.archive.browser.model.vod

sealed interface VodSortOption

data object DateTimeVodSortOption : VodSortOption
data object DurationVodSortOption : VodSortOption
data class RandomStableVodSortOption(val seed: Int): VodSortOption
