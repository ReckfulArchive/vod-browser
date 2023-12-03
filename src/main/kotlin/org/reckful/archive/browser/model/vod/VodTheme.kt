package org.reckful.archive.browser.model.vod

import java.util.*
import kotlin.random.asKotlinRandom

private const val BASE_PATH = "/img/theme/vod"

enum class VodTheme(
    val headerSizePx: Int,
    val headerImgPath: String,
    val backgroundImgPath: String
) {
    DRAKE(
        headerSizePx = 90,
        headerImgPath = "$BASE_PATH/drake-header.png",
        backgroundImgPath = "$BASE_PATH/drake-background.jpg"
    ),

    SHRINE(
        headerSizePx = 90,
        headerImgPath = "$BASE_PATH/shrine-header.png",
        backgroundImgPath = "$BASE_PATH/shrine-background.jpg"
    ),

    GATE(
        headerSizePx = 170,
        headerImgPath = "$BASE_PATH/gate-header.jpg",
        backgroundImgPath = "$BASE_PATH/gate-background.jpg"
    );

    companion object {
        private val random = Random().asKotlinRandom()

        @JvmStatic
        fun random(): VodTheme {
            val idx = random.nextInt(from = 0, until = VodTheme.entries.size)
            return VodTheme.entries[idx]
        }
    }
}
