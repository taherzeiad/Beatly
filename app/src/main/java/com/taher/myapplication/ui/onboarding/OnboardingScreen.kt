package com.beatly.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beatly.model.OnboardingPage
import com.beatly.ui.components.BeatlyPrimaryButton
import com.beatly.ui.components.PageIndicators
import com.beatly.ui.components.RegisterFooter
import com.beatly.ui.theme.BeatlyTheme
import com.beatly.ui.theme.TextWhite

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onContinueFinished: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })

    // Sync pager ↔ ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    if (uiState.pages.isEmpty()) return

    OnboardingContent(
        pages = uiState.pages,
        currentPage = uiState.currentPageIndex,
        pagerState = pagerState,
        onContinue = {
            val finished = viewModel.onContinueClicked()
            if (finished) onContinueFinished()
        },
        onRegisterClicked = onRegisterClicked
    )
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun OnboardingContent(
    pages: List<OnboardingPage>,
    currentPage: Int,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onContinue: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Pager fills entire screen
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageItem(page = pages[pageIndex])
        }

        // Overlay controls pinned to bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            PageIndicators(
                pageCount = pages.size,
                currentPage = currentPage,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // Continue button
            BeatlyPrimaryButton(
                text = "Continue",
                onClick = onContinue
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Register footer
            RegisterFooter(onRegisterClicked = onRegisterClicked)
        }
    }
}

// ── Single onboarding page ─────────────────────────────────────────────────

@Composable
private fun OnboardingPageItem(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay: transparent at top → dark at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.35f to Color.Transparent,
                            0.65f to Color(0xCC1A1A2E),
                            1f to Color(0xFF0F0F1A)
                        )
                    )
                )
        )

        // Text content sits above gradient, in the lower half
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 180.dp), // leave room for controls
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = page.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.description,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = TextWhite.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    BeatlyTheme {
        OnboardingScreen(
            onContinueFinished = {},
            onRegisterClicked = {}
        )
    }
}