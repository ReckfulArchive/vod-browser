package org.reckfularchive.vods.website.model

import org.reckfularchive.vods.website.formatter.DurationFormatter
import java.time.Duration

data class VodSearchChapter(
    val id: Int,
    val name: String,
    val thumbnailUrl: String,

    val duration: Duration,
    val startTimeSec: Int
) {
    val durationFormattedHumanReadableLong = DurationFormatter.toHumanReadableLong(duration)
}
