package org.reckful.archive.browser.service

import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class WebVttService {

    fun createWebVttFile(lines: List<WebVttLine>): String {
        return lines.joinToString(
            prefix = "WEBVTT$WEBVTT_NEWLINE$WEBVTT_NEWLINE",
            separator = "$WEBVTT_NEWLINE$WEBVTT_NEWLINE"
        ) {
            formatWebVttLine(it)
        }
    }

    private fun formatWebVttLine(line: WebVttLine): String {
        val startTime = WEBVTT_TIME_FORMATTER.format(line.startTime)
        val endTime = WEBVTT_TIME_FORMATTER.format(line.endTime)
        return """
            $startTime --> $endTime
            ${line.title.trim()}
        """.trimIndent()
    }

    data class WebVttLine(
        val startTime: LocalTime,
        val endTime: LocalTime,
        val title: String
    )

    internal companion object {
        const val WEBVTT_EMPTY_RESPONSE = "WEBVTT"
        private const val WEBVTT_NEWLINE = "\n"
        private val WEBVTT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    }
}
