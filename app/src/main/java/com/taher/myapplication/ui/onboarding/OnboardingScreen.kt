package com.beatly.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.beatly.ui.theme.BodyMediumRegular
import com.beatly.ui.theme.Gray950
import com.beatly.ui.theme.Headline
import com.beatly.ui.theme.White

// ── Screen entry point (wires ViewModel) ──────────────────────────────────

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onContinueFinished: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })

    // Keep ViewModel in sync when user swipes manually
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
    pagerState: PagerState,
    onContinue: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Full-screen pager
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { index ->
            OnboardingPageItem(page = pages[index])
        }

        // Bottom controls overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PageIndicators(
                pageCount = pages.size,
                currentPage = currentPage,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            BeatlyPrimaryButton(
                text = "Continue", onClick = onContinue
            )

            Spacer(modifier = Modifier.height(12.dp))

            RegisterFooter(onRegisterClicked = onRegisterClicked)
        }
    }
}

// ── Single page ────────────────────────────────────────────────────────────

@Composable
private fun OnboardingPageItem(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Hero image
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient: transparent → Gray950 (matches dark overlay in Figma)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.30f to Color.Transparent,
                            0.60f to Gray950.copy(alpha = 0.80f),
                            1.00f to Gray950
                        )
                    )
                )
        )

        // Text block — sits above bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 250.dp),   // clearance for dots + button + footer
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Headline — Figma: SemiBold 28sp / lh 39.2
            Text(
                text = page.title, style = Headline, color = White, textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Body — Figma: Regular 16sp / lh 25.6
            Text(
                text = page.description,
                fontSize = 14.sp,
                style = BodyMediumRegular,
                color = White.copy(alpha = 0.80f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    BeatlyTheme {
        OnboardingScreen(onContinueFinished = {}, onRegisterClicked = {})
    }
}