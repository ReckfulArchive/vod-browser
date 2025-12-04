package org.reckful.archive.browser.model

import org.reckful.archive.browser.util.formatToDoubleNumber
import java.time.Duration

data class VodChapter(
    val id: Long,
    val name: String,

    val vodId: Long? = null,
    val startTimeSec: Int? = null,
) {
    val startTimeFormatted = startTimeSec?.let {
        Duration.ofSeconds(it.toLong()).formatToDoubleNumber()
    }
}
