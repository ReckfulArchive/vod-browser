package org.reckful.archive.browser.entity

import java.time.Duration
import java.time.LocalDateTime

data class VodEntity(
    val id: Long,
    val externalId: String,
    val title: String,
    val thumbnailUrl: String,
    val dateTime: LocalDateTime,
    val duration: Duration,
    val description: String?,

    /**
     * URL to a single jpg sprite that contains 100 frames of this video with size 160x90
     *
     * See https://www.nuevodevel.com/nuevo/showcase/sprite for mor details
     */
    val previewSpriteUrl: String?
)
