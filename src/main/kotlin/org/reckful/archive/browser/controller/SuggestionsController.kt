package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.reckful.archive.browser.service.VodSuggestionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import kotlin.random.asKotlinRandom

@Controller
@RequestMapping("/suggestions")
class SuggestionsController(
    val vodSuggestionService: VodSuggestionService
) {

    @HxRequest
    @GetMapping("/vod/{vodId}/next")
    fun nextVodsByDate(
        @PathVariable vodId: Long,
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("limit", defaultValue = "5") limit: Int,
        model: Model
    ): String {
        val nextByDate = vodSuggestionService.getNextByDate(vodId, page, limit)
        model.addAttribute("suggestionPage", nextByDate)

        return "vod/suggestion :: nextByDateResults"
    }

    @HxRequest
    @GetMapping("/vod/{vodId}/more")
    fun moreVodsLike(
        @PathVariable vodId: Long,
        @RequestParam("seed", required = false) seed: Int?,
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("limit", defaultValue = "10") limit: Int,
        model: Model
    ): String {
        val seedValue = seed ?: random.nextInt(from = 0, until = 10000)
        model.addAttribute("moreLikeSeed", seedValue)

        val moreLike = vodSuggestionService.getMoreLike(
            vodId = vodId,
            seed = seedValue,
            page = page,
            limit = limit
        )
        model.addAttribute("suggestionPage", moreLike)

        return "vod/suggestion :: moreLikeThisResults"
    }

    private companion object {
        private val random = Random().asKotlinRandom()
    }
}
