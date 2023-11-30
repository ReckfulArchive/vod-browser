package org.reckful.archive.browser.util

internal fun String.withMaxLength(maxLength: Int): String {
    return if (this.length <= maxLength) this
    else "${this.take(maxLength)}..."
}
