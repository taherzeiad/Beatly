package com.taher.beatly.data.mapper

import com.taher.beatly.data.local.room.ArtistEntity
import com.taher.beatly.data.local.room.SongEntity
import com.taher.beatly.data.remote.spotify.SpotifyAlbumItem
import com.taher.beatly.data.remote.spotify.SpotifyArtist
import com.taher.beatly.data.remote.spotify.SpotifyPlaylistItem
import com.taher.beatly.data.remote.spotify.SpotifyTrackItem
import com.taher.beatly.domain.model.*

fun SpotifyTrackItem.toDomain() = Song(
    id = id,
    title = name,
    artistName = artists.firstOrNull()?.name ?: "",
    artistId = artists.firstOrNull()?.id ?: "",
    albumName = album.name,
    imageUrl = album.images.firstOrNull()?.url ?: "",
    previewUrl = preview_url ?: "",
    durationMs = duration_ms
)

fun SpotifyArtist.toDomain() = Artist(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    monthlyListeners = followers.total,
    genres = genres
)

fun SpotifyAlbumItem.toDomain() = Album(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    artistName = artists.firstOrNull()?.name ?: "",
    totalTracks = total_tracks
)

fun SpotifyPlaylistItem.toDomain() = Playlist(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    songCount = tracks.total,
    ownerId = owner.display_name
)

fun Song.toEntity() = SongEntity(
    id = id,
    title = title,
    artistName = artistName,
    artistId = artistId,
    albumName = albumName,
    imageUrl = imageUrl,
    previewUrl = previewUrl,
    durationMs = durationMs,
    isLiked = isLiked,
    isSaved = isSaved
)

fun Artist.toEntity() = ArtistEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    monthlyListeners = monthlyListeners,
    isFollowing = isFollowing
)
