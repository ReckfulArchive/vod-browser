package org.reckful.archive.browser.util

import java.time.Duration

internal fun Duration.formatToHumanReadable(): String {
    val hours = this.toHoursPart()
    val minutes = this.toMinutesPart()
    val seconds = this.toSecondsPart()

    fun hourString() = if (hours == 1) "hour" else "hours"
    fun minuteString() = if (minutes == 1) "minute" else "minutes"
    fun secondString() = if (seconds == 1) "second" else "seconds"

    return if (hours > 0) {
        "$hours ${hourString()} $minutes ${minuteString()}"
    } else if (minutes > 0) {
        "$minutes ${minuteString()} $seconds ${secondString()}"
    } else {
        "$seconds ${secondString()}"
    }
}

internal fun Duration.formatToDoubleNumber(): String {
    val hours = this.toHours() // hours can overflow into days, which we don't want
    val minutes = this.toMinutesPart()
    val seconds = this.toSecondsPart()

    return if (hours > 0) {
        "$hours:${minutes.formatToDoubleDigit()}:${seconds.formatToDoubleDigit()}"
    } else {
        "${minutes.formatToDoubleDigit()}:${seconds.formatToDoubleDigit()}"
    }
}

private fun Int.formatToDoubleDigit(): String {
    if (this <= 0) {
        return "00"
    }
    return if (this > 9) this.toString() else "0$this"
}

internal data class WithDuration<T>(
    val data: T,
    val duration: Duration
)

internal fun <T, K> Iterable<WithDuration<T>>.findWithLongestDurationGroupedBy(uniqueSelector: (T) -> K): T? {
    return this
        .groupBy { uniqueSelector(it.data) }
        .maxByOrNull { group -> group.value.sumOf { it.duration.seconds } }
        ?.value
        ?.firstOrNull()
        ?.data
}
