package com.taher.beatly.ui.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.LibraryRepository
import com.taher.beatly.model.Artist
import com.taher.beatly.ui.components.BeatlyImage
import com.taher.beatly.ui.components.BeatlyTopBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FollowedArtistsUiState(
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FollowedArtistsViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FollowedArtistsUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(BeatlyResult.Success(emptyList<com.taher.beatly.domain.model.Artist>()))
            libraryRepository.getFollowedArtistsFlow(user.id)
        }
        .map { result ->
            when (result) {
                is BeatlyResult.Success -> FollowedArtistsUiState(
                    artists = result.data.map { da ->
                        Artist(
                            id = da.id, name = da.name, imageUrl = da.imageUrl,
                            isVerified = da.isVerified, isFollowing = da.isFollowing,
                            monthlyListeners = da.monthlyListeners
                        )
                    },
                    isLoading = false
                )
                is BeatlyResult.Loading -> FollowedArtistsUiState(isLoading = true)
                else -> FollowedArtistsUiState(isLoading = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FollowedArtistsUiState(isLoading = true))
}

@Composable
fun FollowedArtistsScreen(
    onBackClick: () -> Unit,
    onArtistClick: (String) -> Unit,
    viewModel: FollowedArtistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BeatlyTopBar(title = "Followed Artists", onBackClick = onBackClick)
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.artists, key = { it.id }) { artist ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onArtistClick(artist.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BeatlyImage(
                            url = artist.imageUrl,
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(artist.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
