package org.reckful.archive.browser.controller

import org.reckful.archive.browser.dto.BrowseFilterOptions
import org.reckful.archive.browser.model.browse.*
import org.reckful.archive.browser.service.BrowseVodService
import org.reckful.archive.browser.service.PlaylistService
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class PlaylistController(
    private val playlistService: PlaylistService,
    private val browseVodService: BrowseVodService,
) {
    @GetMapping("playlists")
    @Secured("mod")
    fun getPlaylists(model: Model): String {
        model.addAttribute("playlists", playlistService.getAllPlaylists())
        return "playlist/playlists"
    }

    @GetMapping("/playlist/{playlistId}")
    @Secured("mod")
    fun getPlaylist(
        @PathVariable playlistId: Long,
        model: Model,
        filterOptions: BrowseFilterOptions,
    ): String {
        val playlist = playlistService.getById(playlistId) ?: throw IllegalArgumentException("No playlist with id")
        model.addAttribute("playlist", playlist)

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

        val vodsPage = browseVodService.getPage(
            playlistId = playlistId,
            title = selectedTitle,
            chapterId = selectedChapterId,
            year = selectedYear,
            sort = selectedSort,
            page = 1,
            limit = DEFAULT_LIMIT_PARAM_VALUE
        )
        model.addAttribute("page", vodsPage)

        val chapterOptions = browseVodService.getChapterFilterOptions(
            playlistId = playlistId,
            title = selectedTitle,
            year = selectedYear
        )
        model.addAttribute("chapterFilters", chapterOptions)

        val yearValues = browseVodService.getYearFilterOptions(
            playlistId = playlistId,
            title = selectedTitle,
            chapterId = selectedChapterId
        )
        model.addAttribute("yearFilters", yearValues)

        model.addAttribute("sortFilters", getSortFilters(randomSortSeed = sortSeed))

        return "playlist/playlist"
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

    @PostMapping("playlists")
    @Secured("mod")
    fun createPlaylist(name: String, description: String): String {
        playlistService.createPlaylist(name, description)
        return "redirect:/playlists"
    }
}
