package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import org.reckful.archive.browser.dto.VodDTO
import org.reckful.archive.browser.service.ChapterService
import org.reckful.archive.browser.service.VodService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

// TODO extract into a separate module?
@Controller
@RequestMapping("/vod")
class VodController(
    private val chapterService: ChapterService,
    private val vodService: VodService
) {
    @GetMapping("/{vodId}")
    fun vodPage(
        @PathVariable vodId: Long,
        @RequestParam("start", required = false) startTimeSeconds: Int?,
        model: Model
    ): HtmxResponse {
        val vodDetails = requireNotNull(vodService.getVodDetailsById(vodId)) {
            "Unable to find a vod with id: $vodId"
        }
        model.addAttribute("vodDetails", vodDetails)
        model.addAttribute("startTimeSeconds", startTimeSeconds)

        return HtmxResponse.builder()
            .view("vod/index")
            .build()
    }

    @PostMapping("/report")
    fun report(
        @RequestParam("id") vodId: Long,
        @RequestParam("message") message: String,
    ): HtmxResponse {
        require(message.isNotEmpty()) {
            "Message must not be empty"
        }
        vodService.saveReport(vodId, "USER_REPORT", message)

        return HtmxResponse.builder()
            .build()
    }

    @GetMapping("/{vodId}/chapters.vtt", produces = ["text/plain"])
    @ResponseBody
    fun getVodChaptersWebVtt(
        @PathVariable("vodId") vodId: Long
    ): String {
        return chapterService.getChaptersWebVttFile(vodId)
    }

    @GetMapping("/rest/archive", produces = ["application/json"])
    @ResponseBody
    fun find(
        @RequestParam("fileName", required = false) archiveFileName: String? = null
    ): List<VodDTO> {
        if (archiveFileName == null) return emptyList()
        return vodService.findArchiveVodsByFileName(archiveFileName)
    }
}
