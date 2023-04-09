package org.reckful.archive.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

class AllVideosPageParser(
    filesDirectory: File
) : VideosPageParser(
    htmlPage = filesDirectory.resolve("all-videos-page/index.html"),
    pageFilesDir = filesDirectory.resolve("all-videos-page/index_files")
)

/**
 * Beware that some VODs were converted to highlights so that they don't get lost
 */
class HighlightsPageParser(
    filesDirectory: File
) : VideosPageParser(
    htmlPage = filesDirectory.resolve("highlights-page/index.html"),
    pageFilesDir = filesDirectory.resolve("highlights-page/index_files")
)

/**
 * Parses content from https://www.twitch.tv/reckful/videos
 */
abstract class VideosPageParser(
    private val htmlPage: File,
    private val pageFilesDir: File,
) {
    init {
        check(htmlPage.exists())
        check(pageFilesDir.exists())
    }

    fun getTowerCards(): List<VideoTowerCard> {
        val towerCardElements = Jsoup.parse(getIndexFileContents())
            .select("div .ScTower-sc-1sjzzes-0 > div")
            .select("div[data-a-target^=video-tower-card]")

        return towerCardElements.map { it.toVideoTowerCard() }
    }

    private fun getIndexFileContents(): String {
        return htmlPage.readText()
            .takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("Expected the main html page to not be empty: $htmlPage")
    }

    private fun Element.toVideoTowerCard(): VideoTowerCard {
        val header = this.select("h3")
        val videoName = header.text()
        val link = header.parents()[0].attr("href").substringBefore("?")
        val date = this.select("img[class=tw-image][title]").attr("title")
        return VideoTowerCard(
            name = videoName,
            link = link,
            date = date,
            thumbnail = getThumbnailImage()
        )
    }

    private fun Element.getThumbnailImage(): File? {
        val imgSrc = this.select("img[class=tw-image][title]").attr("src")
        val fileName = imgSrc.substringAfterLast("/")
        return pageFilesDir.resolve(fileName)
            .takeIf { it.exists() }
    }
}

data class VideoTowerCard(
    val name: String,
    val link: String,
    val date: String,
    val thumbnail: File?,
)
