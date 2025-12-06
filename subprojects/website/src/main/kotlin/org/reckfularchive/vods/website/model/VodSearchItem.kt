package org.reckfularchive.vods.website.model

import org.reckfularchive.vods.website.formatter.DurationFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class VodSearchItem(
    val id: Int,
    val title: String,

    val date: LocalDate,
    val duration: Duration,

    val thumbnailUrl: String,

    val chapters: List<VodSearchChapter>,
) {
    val dateFormattedIso: String = DateTimeFormatter.ISO_DATE.format(date)
    val durationFormattedNumberHoursMinutesSeconds: String = DurationFormatter.toNumberHoursMinutesSeconds(duration)

    val longestChapter = chapters
        .groupBy { it.id }
        .maxByOrNull { group -> group.value.sumOf { it.duration.seconds } }
        ?.value
        ?.firstOrNull()
}
