package org.reckful.archive.browser.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.reckful.archive.browser.dto.BrowseFilterOptions
import org.reckful.archive.browser.model.browse.*
import org.reckful.archive.browser.service.BrowseVodService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

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
        // user requests should not contain the seed, it's an internal detail,
        // so this request is not expected to have any and we need to generate it for the filter
        val sortSeed = RandomStableBrowseFilterSort.randomSeed()

        val validatedFilterOptions = when {
            filterOptions.hasRandomSortWithoutSeed() -> filterOptions.copy(
                sort = RandomStableBrowseFilterSort.createFilterValue(sortSeed)
            )
            else -> filterOptions
        }

        val selectedTitle = validatedFilterOptions.parseTitle()
        val selectedChapterId = validatedFilterOptions.parseChapterId()
        val selectedYear = validatedFilterOptions.parseYear()
        val selectedSort = validatedFilterOptions.parseSort()
        val selectedPlaylistId = filterOptions.playlist ?: 10

        val vodsPage = browseVodService.getPage(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = selectedSort,
            page = 1,
            limit = DEFAULT_LIMIT_PARAM_VALUE
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)

        val yearValues = browseVodService.getYearFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId
        )
        model.addAttribute("yearFilters", yearValues)

        model.addAttribute("sortFilters", getSortFilters(randomSortSeed = sortSeed))

        model.addAttribute("playlistId", selectedPlaylistId)

        return "browse/index"
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
            playlistId = requireNotNull(filterOptions.playlist),
            title = filterOptions.parseTitle(),
            chapterId = filterOptions.parseChapterId(),
            year = filterOptions.parseYear(),
            sort = filterOptions.parseSort(),
            page = page,
            limit = limit
        )
        model.addAttribute("page", vodsPage)
        return "browse/vods :: vods"
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
        val selectedPlaylistId = requireNotNull(filterOptions.playlist)

        val vodsPage = browseVodService.getPage(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)

        val yearValues = browseVodService.getYearFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId
        )
        model.addAttribute("yearFilters", yearValues)

        model.addAttribute("playlistId", selectedPlaylistId)

        return HtmxResponse.builder()
            .view("browse/vods :: search-results")
            .view("browse/filters :: form-filters-chapter")
            .view("browse/filters :: form-filters-year")
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
        val selectedPlaylistId = requireNotNull(filterOptions.playlist)

        val vodsPage = browseVodService.getPage(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val yearValues = browseVodService.getYearFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId
        )
        model.addAttribute("yearFilters", yearValues)

        model.addAttribute("playlistId", selectedPlaylistId)

        return HtmxResponse.builder()
            .view("browse/vods :: search-results")
            .view("browse/filters :: form-filters-year")
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
        val selectedPlaylistId = requireNotNull(filterOptions.playlist)

        val vodsPage = browseVodService.getPage(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            playlistId = selectedPlaylistId,
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)

        model.addAttribute("playlistId", selectedPlaylistId)

        return HtmxResponse.builder()
            .view("browse/vods :: search-results")
            .view("browse/filters :: form-filters-chapter")
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
            playlistId = requireNotNull(filterOptions.playlist),
            title = filterOptions.parseTitle(),
            chapterId = filterOptions.parseChapterId(),
            year = filterOptions.parseYear(),
            sort = filterOptions.parseSort(),
            page = 1,
            limit = 30
        )
        model.addAttribute("page", vodsPage)

        return HtmxResponse.builder()
            .view("browse/vods :: search-results")
            .triggerAfterSwap("selectpicker-render")
            .pushFilterOptionsUrl(filterOptions)
            .build()
    }

    private fun getSortFilters(randomSortSeed: Int): List<BrowseFilterSort> {
        return listOf(
            DateDescBrowseFilterSort,
            DateAscBrowseFilterSort,
            DurationDescBrowseFilterSort,
            DurationAscBrowseFilterSort,
            RandomStableBrowseFilterSort(randomSortSeed)
        )
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
