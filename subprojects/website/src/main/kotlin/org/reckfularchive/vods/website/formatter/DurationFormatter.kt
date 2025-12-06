package org.reckfularchive.vods.website.formatter

import java.time.Duration

object DurationFormatter {

    /**
     * Formats [duration] as either "13:28:08" or "28:08".
     *
     * If duration is >24 hours, it doesn't roll over, so it will be "26:28:08"
     */
    fun toNumberHoursMinutesSeconds(duration: Duration): String {
        val hours = duration.toHours() // hours can be >25, so we are not using toHoursPart()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()

        return if (hours > 0) {
            "$hours:${minutes.toDoubleDigit()}:${seconds.toDoubleDigit()}"
        } else {
            "${minutes.toDoubleDigit()}:${seconds.toDoubleDigit()}"
        }
    }

    private fun Int.toDoubleDigit(): String {
        if (this <= 0) {
            return "00"
        }
        return if (this > 9) this.toString() else "0$this"
    }

    /**
     * Formats [duration] as either "2 hours 37 minutes" or "51 minutes 47 seconds"
     *
     * If duration is >24 hours, it doesn't roll over, so it will be "26 hours 37 minutes"
     */
    fun toHumanReadableLong(duration: Duration): String {
        val hours = duration.toHours() // hours can be >25, so we are not using toHoursPart()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()

        val hourString = if (hours == 1L) "hour" else "hours"
        val minuteString = if (minutes == 1) "minute" else "minutes"
        val secondString = if (seconds == 1) "second" else "seconds"

        return if (hours > 0) {
            "$hours $hourString $minutes $minuteString"
        } else if (minutes > 0) {
            "$minutes $minuteString $seconds $secondString"
        } else {
            "$seconds $secondString"
        }
    }
}
