package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import org.reckful.archive.browser.dto.EditVodForm
import org.reckful.archive.browser.dto.VodDTO
import org.reckful.archive.browser.service.ChapterService
import org.reckful.archive.browser.service.PlaylistService
import org.reckful.archive.browser.service.VodService
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

// TODO extract into a separate module?
@Controller
@RequestMapping("/vod")
class VodController(
    private val chapterService: ChapterService,
    private val vodService: VodService,
    private val playlistService: PlaylistService,
) {
    @GetMapping("/{vodId}")
    fun vodPage(
        @PathVariable vodId: Long,
        @RequestParam("start", required = false) startTimeSeconds: Int?,
        @AuthenticationPrincipal userDetails: UserDetails? = null,
        model: Model
    ): String {
        val vodDetails = requireNotNull(vodService.getVodDetailsById(vodId)) {
            "Unable to find a vod with id: $vodId"
        }
        model.addAttribute("vodDetails", vodDetails)
        model.addAttribute("startTimeSeconds", startTimeSeconds)

        val authorities = userDetails?.authorities ?: emptyList()
        if (authorities.any { it.authority == "mod" }) {
            val allPlaylists = playlistService.getAllPlaylists()
            model.addAttribute("allPlaylists", allPlaylists)

            val selectedPlaylistIds = playlistService.getAllByVodId(vodId).map { it.id }
            model.addAttribute("selectedPlaylistIds", selectedPlaylistIds)
        }

        return "vod/index"
    }

    @PostMapping("/{vodId}/edit")
    @Secured("mod")
    fun edit(@PathVariable vodId: Long, editVodForm: EditVodForm): String {
        require(editVodForm.title.length >= 2) {
            "VOD title length must be >= 2"
        }
        require(vodId == editVodForm.id) {
            "Vod ID in the path and the form don't match: $vodId vs ${editVodForm.id}"
        }
        vodService.update(editVodForm)
        return "redirect:/vod/{vodId}"
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
