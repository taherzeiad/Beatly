package com.taher.beatly.ui.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenreUiState(
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class GenreViewModel @Inject constructor(
    repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenreUiState())
    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getGenres().collect { genres ->
                _uiState.value = GenreUiState(genres = genres, isLoading = false)
            }
        }
    }
}
