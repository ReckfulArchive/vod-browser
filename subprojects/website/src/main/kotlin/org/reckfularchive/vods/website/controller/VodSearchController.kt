package org.reckfularchive.vods.website.controller

import org.reckfularchive.vods.website.service.VodSearchService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class VodSearchController(
    private val vodSearchService: VodSearchService
) {

    @GetMapping("/search")
    fun search(model: Model): String {
        val searchResult = vodSearchService.search(1, 30)
        model.addAttribute("searchResult", searchResult)
        return "fragments/vod-search-results :: vods"
    }
}
