package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.reckful.archive.browser.dto.BrowseFilterOptions
import org.reckful.archive.browser.model.browse.BrowseFilterSort
import org.reckful.archive.browser.service.BrowseVodService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

const val DEFAULT_CHAPTER_FILTER_VALUE = "all"
const val DEFAULT_YEAR_FILTER_VALUE = "all"
const val DEFAULT_SORT_FILTER_VALUE = "date_desc"

const val DEFAULT_LIMIT_PARAM_VALUE = 30

// TODO clean up copy-paste
@Controller
class BrowseVodController(
    private val browseVodService: BrowseVodService
) {
    @GetMapping("/")
    fun browse(
        filterOptions: BrowseFilterOptions,
        model: Model
    ): String {
        val selectedTitle = filterOptions.parseTitle()
        val selectedChapterId = filterOptions.parseChapterId()
        val selectedYear = filterOptions.parseYear()
        val selectedSort = filterOptions.parseSort()

        model.addAttribute("selectedTitle", filterOptions.title)

        val vodsPage = browseVodService.getPage(
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = selectedSort,
            page = 1,
            limit = DEFAULT_LIMIT_PARAM_VALUE
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)
        model.addAttribute("selectedChapterValue", selectedChapterId ?: DEFAULT_CHAPTER_FILTER_VALUE)

        val yearValues = browseVodService.getYearFilterOptions(title = selectedTitle, chapterId = selectedChapterId)
        model.addAttribute("yearFilters", yearValues)
        model.addAttribute("selectedYearValue", selectedYear ?: DEFAULT_YEAR_FILTER_VALUE)

        model.addAttribute("sortFilters", BrowseFilterSort.entries.toList())
        model.addAttribute("selectedSortValue", selectedSort.filterValue)

        return "browse"
    }

    @HxRequest
    @GetMapping("/page")
    fun browsePage(
        filterOptions: BrowseFilterOptions,
        @RequestParam("page") page: Int,
        @RequestParam("limit", required = false, defaultValue = DEFAULT_LIMIT_PARAM_VALUE.toString()) limit: Int,
        model: Model,
    ): String {
        require(page > 0) {
            "Page is expected to be positive and start from 1"
        }
        require(limit in 1..250) {
            "Limit is expected to be positive and be less than 250"
        }
        val vodsPage = browseVodService.getPage(
            title = filterOptions.parseTitle(),
            chapterId = filterOptions.parseChapterId(),
            year = filterOptions.parseYear(),
            sort = filterOptions.parseSort(),
            page = page,
            limit = limit
        )
        model.addAttribute("page", vodsPage)
        return "browse-vods :: vods"
    }

    @GetMapping("/")
    @HxRequest(target = "search-results", triggerName = "title")
    fun filterTitle(
        filterOptions: BrowseFilterOptions,
        model: Model,
        request: HtmxRequest
    ): HtmxResponse {
        val selectedTitle = filterOptions.parseTitle()
        val selectedChapterId = filterOptions.parseChapterId()
        val selectedYear = filterOptions.parseYear()

        val vodsPage = browseVodService.getPage(
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)
        model.addAttribute("selectedChapterValue", selectedChapterId ?: DEFAULT_CHAPTER_FILTER_VALUE)

        val yearValues = browseVodService.getYearFilterOptions(title = selectedTitle, chapterId = selectedChapterId)
        model.addAttribute("yearFilters", yearValues)
        model.addAttribute("selectedYearValue", selectedYear ?: DEFAULT_YEAR_FILTER_VALUE)

        return HtmxResponse.builder()
            .view("browse-vods :: search-results")
            .view("browse-filters :: form-filters-chapter")
            .view("browse-filters :: form-filters-year")
            .triggerAfterSwap("selectpicker-render")
            .pushFilterOptionsUrl(filterOptions)
            .build()
    }

    @GetMapping("/")
    @HxRequest(target = "search-results", triggerName = "chapter")
    fun filterChapter(
        filterOptions: BrowseFilterOptions,
        model: Model,
        request: HtmxRequest
    ): HtmxResponse {
        val selectedTitle = filterOptions.parseTitle()
        val selectedChapterId = filterOptions.parseChapterId()
        val selectedYear = filterOptions.parseYear()

        val vodsPage = browseVodService.getPage(
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val yearValues = browseVodService.getYearFilterOptions(title = selectedTitle, chapterId = selectedChapterId)
        model.addAttribute("yearFilters", yearValues)
        model.addAttribute("selectedYearValue", selectedYear ?: DEFAULT_YEAR_FILTER_VALUE)

        return HtmxResponse.builder()
            .view("browse-vods :: search-results")
            .view("browse-filters :: form-filters-year")
            .triggerAfterSwap("selectpicker-render")
            .pushFilterOptionsUrl(filterOptions)
            .build()
    }

    @GetMapping("/")
    @HxRequest(target = "search-results", triggerName = "year")
    fun filterYear(
        filterOptions: BrowseFilterOptions,
        model: Model,
        request: HtmxRequest
    ): HtmxResponse {
        val selectedTitle = filterOptions.parseTitle()
        val selectedChapterId = filterOptions.parseChapterId()
        val selectedYear = filterOptions.parseYear()

        val vodsPage = browseVodService.getPage(
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)
        model.addAttribute("selectedChapterValue", selectedChapterId ?: DEFAULT_CHAPTER_FILTER_VALUE)

        return HtmxResponse.builder()
            .view("browse-vods :: search-results")
            .view("browse-filters :: form-filters-chapter")
            .triggerAfterSwap("selectpicker-render")
            .pushFilterOptionsUrl(filterOptions)
            .build()
    }

    @GetMapping("/")
    @HxRequest(target = "search-results", triggerName = "sort")
    fun filterSort(
        filterOptions: BrowseFilterOptions,
        model: Model,
        request: HtmxRequest
    ): HtmxResponse {
        val vodsPage = browseVodService.getPage(
            title = filterOptions.parseTitle(),
            chapterId = filterOptions.parseChapterId(),
            year = filterOptions.parseYear(),
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        return HtmxResponse.builder()
            .view("browse-vods :: search-results")
            .triggerAfterSwap("selectpicker-render")
            .pushFilterOptionsUrl(filterOptions)
            .build()
    }

    @HxRequest
    @GetMapping("/browse/vod/{vodId}")
    fun vod(
        @PathVariable("vodId") vodId: Long,
        @RequestParam("start", required = false) startTimeSeconds: Int?,
        model: Model
    ): HtmxResponse {
        val vodDetails = requireNotNull(browseVodService.getVodDetailsById(vodId)) {
            "Unable to find a vod with id: $vodId"
        }
        model.addAttribute("vodDetails", vodDetails)
        model.addAttribute("startTimeSeconds", startTimeSeconds)
        return HtmxResponse.builder()
            .view("browse-modal-vod :: modal")
            .triggerAfterSettle("browse-modal-vod-settled")
            .build()
    }
}

private fun HtmxResponse.Builder.pushFilterOptionsUrl(
    filterOptions: BrowseFilterOptions,
    defaultUrl: String = "/"
): HtmxResponse.Builder {
    val queryParameters = filterOptions.buildQueryParams()
    return if (queryParameters.isNotBlank()) {
        this.pushUrl(queryParameters)
    } else {
        this.pushUrl(defaultUrl)
    }
}
