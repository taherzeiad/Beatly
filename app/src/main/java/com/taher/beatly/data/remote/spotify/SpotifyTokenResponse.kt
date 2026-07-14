package com.taher.beatly.data.remote.spotify


import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ── Retrofit DTOs ──────────────────────────────────────────────────────────

data class SpotifyTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class SpotifyRecommendationsResponse(val tracks: List<SpotifyTrackItem>)
data class SpotifyTracksResponse(val tracks: SpotifyTrackPaging)
data class SpotifyTrackPaging(val items: List<SpotifyTrackItem>)

data class SpotifyTrackItem(
    val id: String,
    val name: String,
    val preview_url: String?,
    val duration_ms: Long,
    val artists: List<SpotifyArtistSimple>,
    val album: SpotifyAlbum
)

data class SpotifyArtistSimple(val id: String, val name: String)

data class SpotifyAlbum(
    val name: String,
    val images: List<SpotifyImage>
)

data class SpotifyImage(val url: String, val width: Int, val height: Int)

data class SpotifyArtist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val genres: List<String>,
    val followers: SpotifyFollowers
)

data class SpotifyFollowers(val total: Long)

data class SpotifyArtistResponse(val artists: SpotifyArtistPaging)
data class SpotifyArtistPaging(val items: List<SpotifyArtist>)

data class SpotifyArtistTracksResponse(val tracks: List<SpotifyTrackItem>)

data class SpotifySearchResponse(
    val tracks: SpotifyTrackPaging? = null,
    val artists: SpotifyArtistPaging? = null,
    val albums: SpotifyAlbumPaging? = null,
    val playlists: SpotifyPlaylistPaging? = null
)

data class SpotifyAlbumPaging(val items: List<SpotifyAlbumItem>)
data class SpotifyAlbumItem(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val artists: List<SpotifyArtistSimple>,
    val total_tracks: Int
)

data class SpotifyPlaylistPaging(val items: List<SpotifyPlaylistItem>)
data class SpotifyPlaylistItem(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val owner: SpotifyUserSimple,
    val tracks: SpotifyPlaylistTracks
)

data class SpotifyUserSimple(val display_name: String)
data class SpotifyPlaylistTracks(val total: Int)

// ── Retrofit Service ───────────────────────────────────────────────────────

interface SpotifyApiService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "track,artist,album,playlist",
        @Query("limit") limit: Int = 20,
        @Header("Authorization") token: String
    ): SpotifySearchResponse

    @GET("artists/{id}")
    suspend fun getArtist(
        @Path("id") artistId: String,
        @Header("Authorization") token: String
    ): SpotifyArtist

    @GET("artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: String,
        @Query("market") market: String = "US",
        @Header("Authorization") token: String
    ): SpotifyArtistTracksResponse

    @GET("browse/featured-playlists")
    suspend fun getFeaturedPlaylists(
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String
    ): Any  // parse as needed

    @GET("browse/new-releases")
    suspend fun getNewReleases(
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String
    ): SpotifyNewReleasesResponse

    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("seed_genres") genres: String = "pop,hip-hop",
        @Query("limit") limit: Int = 20,
        @Header("Authorization") token: String
    ): SpotifyRecommendationsResponse

    @GET("albums/{id}/tracks")
    suspend fun getAlbumTracks(
        @Path("id") albumId: String,
        @Header("Authorization") token: String
    ): SpotifyAlbumTracksResponse

    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") playlistId: String,
        @Header("Authorization") token: String
    ): SpotifyPlaylistTracksResponse
}

data class SpotifyNewReleasesResponse(val albums: SpotifyAlbumPaging)
data class SpotifyAlbumTracksResponse(val items: List<SpotifyTrackItem>)
data class SpotifyPlaylistTracksResponse(val items: List<SpotifyPlaylistTrackItem>)
data class SpotifyPlaylistTrackItem(val track: SpotifyTrackItem)

// ── Token service ──────────────────────────────────────────────────────────

interface SpotifyTokenService {
    @POST("token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Header("Authorization") basicAuth: String
    ): SpotifyTokenResponse
}