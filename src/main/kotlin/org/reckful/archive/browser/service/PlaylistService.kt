package org.reckful.archive.browser.service

import org.reckful.archive.browser.entity.PlaylistEntity
import org.reckful.archive.browser.model.Playlist
import org.reckful.archive.browser.model.vod.DateTimeVodSortOption
import org.reckful.archive.browser.repository.PlaylistRepository
import org.reckful.archive.browser.repository.VodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
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
            .map { it.toModel() }
    }

    // TODO fix
    @Transactional
    fun getById(id: Long): Playlist? = getAllPlaylists().firstOrNull { it.id == id }

    @Transactional
    fun getAllByVodId(vodId: Long): List<Playlist> {
        return playlistRepository.findAllByVodId(vodId)
            .map { it.toModel() }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun changeVodPlaylists(vodId: Long, newPlaylistIds: List<Long>) {
        val currentPlaylistIds = getAllByVodId(vodId).map { it.id }

        currentPlaylistIds.forEach { currentPlaylistId ->
            if (!newPlaylistIds.contains(currentPlaylistId)) {
                playlistRepository.unlinkVodFromPlaylist(vodId, currentPlaylistId)
            }
        }
        newPlaylistIds.forEach { newPlaylistId ->
            if (!currentPlaylistIds.contains(newPlaylistId)) {
                playlistRepository.linkVodToPlaylist(vodId, newPlaylistId)
            }
        }
    }

    private fun PlaylistEntity.toModel(): Playlist {
        val firstVodFromPlaylist = vodRepository.findVods(
            playlistIds = listOf(this.idNotNull),
            sortBy = DateTimeVodSortOption,
            page = 1,
            limit = 1
        ).firstOrNull()

        // TODO add ability to upload custom thumbnails
        val thumbnailUrl = firstVodFromPlaylist?.thumbnailUrl
            ?: DEFAULT_PLAYLIST_THUMBNAIL_URL

        val vodCount = vodRepository.countVods(this.idNotNull)

        return Playlist(
            id = this.idNotNull,
            name = this.name,
            description = this.description,
            thumbnailUrl = thumbnailUrl,
            vodCount = vodCount
        )
    }

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
