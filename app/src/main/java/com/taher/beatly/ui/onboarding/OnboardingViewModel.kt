package com.taher.beatly.ui.onboarding

import androidx.lifecycle.ViewModel
import com.taher.beatly.model.OnboardingPage
import com.taher.beatly.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val pages          : List<OnboardingPage> = emptyList(),
    val currentPageIndex: Int     = 0,
    val isLastPage     : Boolean  = false
)

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val onboardingPages = listOf(
        OnboardingPage(
            title       = "Your Music, Anytime, Anywhere",
            description = "Enjoy your favorite tracks without limits—stream \n online \n or listen offline.",
            imageRes    = R.drawable.onboarding_1
        ),
        OnboardingPage(
            title       = "Endless Music, Zero Restrictions",
            description = "Discover and play the songs you love, \n wherever you are.",
            imageRes    = R.drawable.onboarding_2
        ),
        OnboardingPage(
            title       = "Offline or Online, It's All Yours",
            description = "Download your playlists and enjoy uninterrupted \n music anytime.",
            imageRes    = R.drawable.onboarding_3
        )
    )

    init {
        _uiState.update { it.copy(pages = onboardingPages) }
    }

    // ✅ يُستدعى فقط من LaunchedEffect عند تغيير صفحة الـ Pager (سحب أو زر)
    fun onPageChanged(index: Int) {
        _uiState.update { state ->
            state.copy(
                currentPageIndex = index,
                isLastPage       = index == onboardingPages.size - 1
            )
        }
    }
}