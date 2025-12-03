package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.PlaylistEntity
import org.reckful.archive.browser.model.Playlist
import org.reckful.archive.browser.model.vod.DateTimeVodSortOption
import org.reckful.archive.browser.repository.PlaylistRepository
import org.reckful.archive.browser.repository.VodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val vodRepository: VodRepository
) {

    @Transactional
    fun getAllPlaylists(): List<Playlist> {
        val playlistEntities = playlistRepository.findAll()
        return playlistEntities
            .filter { it.name != "Reserved" }
            .map {
                val firstVodFromPlaylist = vodRepository.findVods(
                    playlistIds = listOf(it.idNotNull),
                    sortBy = DateTimeVodSortOption,
                    page = 1,
                    limit = 1
                ).firstOrNull()

                // TODO add ability to upload custom thumbnails
                val thumbnailUrl = firstVodFromPlaylist?.thumbnailUrl
                    ?: DEFAULT_PLAYLIST_THUMBNAIL_URL

                val vodCount = vodRepository.countVods(it.idNotNull)

                Playlist(
                    id = it.idNotNull,
                    name = it.name,
                    description = it.description,
                    thumbnailUrl = thumbnailUrl,
                    vodCount = vodCount
                )
            }
    }

    // TODO fix
    @Transactional
    fun getById(id: Long): Playlist? = getAllPlaylists().firstOrNull { it.id == id }

    fun createPlaylist(name: String, description: String) {
        require(name.isNotBlank()) {
            "Name cannot be blank"
        }
        val entity = PlaylistEntity(
            name = name,
            description = description
        )
        playlistRepository.save(entity)
    }

    private companion object {
        private const val DEFAULT_PLAYLIST_THUMBNAIL_URL =
            "https://reckfularchive.github.io/twitch-metadata/files/thumbnails/id/1641.jpg"
    }
}
