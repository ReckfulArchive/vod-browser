package org.reckful.archive.browser.controller

import org.reckful.archive.browser.service.ChapterService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class VodController(
    private val chapterService: ChapterService
) {
    @GetMapping("/vod/{vodId}/chapters.vtt", produces = ["text/plain"])
    @ResponseBody
    fun getVodChaptersWebVtt(
        @PathVariable vodId: Long
    ): String {
        return chapterService.getChaptersWebVttFile(vodId)
    }
}
