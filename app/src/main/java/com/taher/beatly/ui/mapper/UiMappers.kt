package com.taher.beatly.ui.mapper

import com.taher.beatly.domain.model.Artist as DomainArtist
import com.taher.beatly.domain.model.Song as DomainSong
import com.taher.beatly.domain.model.Album as DomainAlbum
import com.taher.beatly.domain.model.Playlist as DomainPlaylist
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import com.taher.beatly.model.Album
import com.taher.beatly.model.Playlist

fun DomainSong.toUi() = Song(
    id = id,
    title = title,
    artistName = artistName,
    artistId = artistId,
    imageUrl = imageUrl,
    previewUrl = previewUrl,
    durationMs = durationMs,
    isLiked = isLiked,
    isSaved = isSaved,
)

fun DomainArtist.toUi() = Artist(
    id = id,
    name = name,
    imageUrl = imageUrl,
    isVerified = isVerified,
    isFollowing = isFollowing,
    monthlyListeners = monthlyListeners,
)

fun DomainAlbum.toUi() = Album(
    id = id,
    name = name,
    artistName = artistName,
    imageUrl = imageUrl,
    totalTracks = totalTracks
)

fun DomainPlaylist.toUi() = Playlist(
    id = id,
    name = name,
    ownerName = ownerId,
    imageUrl = imageUrl,
    songCount = songCount
)
