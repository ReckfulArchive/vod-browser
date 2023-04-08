package org.reckful.archive.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

private const val INDEX_FILES_PATH = "/all-videos-page/index_files"
private const val INDEX_HTML_PATH = "/all-videos-page/index.html"

data class VideoTowerCard(
    val name: String,
    val link: String,
    val date: String,
    val thumbnail: File?,
)

/**
 * Parses content from https://www.twitch.tv/reckful/videos?filter=all&sort=time
 * that was scrolled down to the very bottom and saved on April 7th, 2023.
 */
class TowerCardParser {

    fun getTowerCards(): List<VideoTowerCard> {
        val towerCardElements = Jsoup.parse(getIndexFileContents())
            .select("div .ScTower-sc-1sjzzes-0 > div")
            .select("div[data-a-target^=video-tower-card]")

        return towerCardElements.map { it.toVideoTowerCard() }
    }

    private fun getIndexFileContents(): String {
        return this.javaClass.getResource(INDEX_HTML_PATH)
            ?.readText()
            ?.takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("Expected the index file to exist and not be empty")
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
        val fileResourcePath = "$INDEX_FILES_PATH/$fileName"
        val fileResource = this::class.java.getResource(fileResourcePath)
            ?: return null

        return File(fileResource.file).takeIf { it.exists() }
            ?: throw IllegalStateException("Thumbnail file doesn't exist")
    }
}



