package org.reckfularchive.vods.website.service

import org.reckfularchive.vods.website.model.VodSearchChapter
import org.reckfularchive.vods.website.model.VodSearchItem
import org.reckfularchive.vods.website.model.VodSearchResult
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random

@Service
class VodSearchService {

    fun search(page: Int, limit: Int): VodSearchResult {
        return generateSearchResult(page, limit)
    }

    private fun generateSearchResult(page: Int, limit: Int): VodSearchResult {
        return VodSearchResult(
            items = generateSequence { generateVodSearchItem() }.take(limit).toList(),
            pageNum = page,
            limit = limit
        )
    }

    private fun generateVodSearchItem(): VodSearchItem {
        return VodSearchItem(
            id = Random.nextInt(0, 1000),
            title = "reckful - moo",
            date = LocalDate.of(2019, Month.JUNE, 10),
            duration = Duration.ofSeconds(29327),
            thumbnailUrl = "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/id/1711.jpg",
            chapters = listOf(
                VodSearchChapter(
                    id = 1,
                    name = "Just Chatting",
                    thumbnailUrl = "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/chapters/25.jpg",
                    duration = Duration.ofSeconds(24840),
                    startTimeSec = 0
                ),
                VodSearchChapter(
                    id = 2,
                    name = "Mario Kart 8",
                    thumbnailUrl = "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/chapters/29.jpg",
                    duration = Duration.ofSeconds(1373),
                    startTimeSec = 24840
                ),
                VodSearchChapter(
                    id = 3,
                    name = "Super Smash Bros. Ultimate",
                    thumbnailUrl = "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/chapters/47.jpg",
                    duration = Duration.ofSeconds(3096),
                    startTimeSec = 26213
                )
            ).drop(Random.nextInt(0, 3))
        )
    }
}
