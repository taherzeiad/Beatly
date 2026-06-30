package com.beatly.ui.onboarding

import androidx.lifecycle.ViewModel
import com.beatly.model.OnboardingPage
import com.taher.myapplication.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val pages: List<OnboardingPage> = emptyList(),
    val currentPageIndex: Int = 0,
    val isLastPage: Boolean = false
)

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val onboardingPages = listOf(
        OnboardingPage(
            title = "Your Music, Anytime, Anywhere",
            description = "Enjoy your favorite tracks without limits—stream online or listen offline.",
            imageRes = 0//R.drawable.onboarding_1
        ),
        OnboardingPage(
            title = "Endless Music, Zero Restrictions",
            description = "Discover and play the songs you love, wherever you are.",
            imageRes = 0//R.drawable.onboarding_2
        ),
        OnboardingPage(
            title = "Offline or Online, It's All Yours",
            description = "Download your playlists and enjoy uninterrupted music anytime.",
            imageRes = 0//R.drawable.onboarding_3
        )
    )

    init {
        _uiState.update { state ->
            state.copy(pages = onboardingPages)
        }
    }

    fun onContinueClicked(): Boolean {
        val currentIndex = _uiState.value.currentPageIndex
        return if (currentIndex < onboardingPages.size - 1) {
            _uiState.update { state ->
                state.copy(
                    currentPageIndex = currentIndex + 1,
                    isLastPage = currentIndex + 1 == onboardingPages.size - 1
                )
            }
            false // not done yet
        } else {
            true // navigate to registration/home
        }
    }

    fun onPageChanged(index: Int) {
        _uiState.update { state ->
            state.copy(
                currentPageIndex = index,
                isLastPage = index == onboardingPages.size - 1
            )
        }
    }
}