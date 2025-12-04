package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.reckful.archive.browser.service.ChapterService
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/chapters")
class ChapterController(
    private val chapterService: ChapterService
) {
    @HxRequest
    @PostMapping
    @Secured("mod")
    fun addChapter(
        vodId: Long,
        chapterId: Long,
        startTimeSec: Int,
        model: Model,
    ): HtmxResponse {
        chapterService.addChapter(vodId, chapterId, startTimeSec)
        model.addAttribute("vodChapters", chapterService.getChapters(vodId))
        return HtmxResponse.builder()
            .view("vod/mod-tools :: chapters-table")
            .build()
    }

    @HxRequest
    @DeleteMapping
    @Secured("mod")
    fun removeChapter(
        vodId: Long,
        chapterId: Long,
        startTimeSec: Int,
        model: Model,
    ): HtmxResponse {
        chapterService.removeChapter(vodId, chapterId, startTimeSec)
        model.addAttribute("vodChapters", chapterService.getChapters(vodId))
        return HtmxResponse.builder()
            .view("vod/mod-tools :: chapters-table")
            .build()
    }
}
