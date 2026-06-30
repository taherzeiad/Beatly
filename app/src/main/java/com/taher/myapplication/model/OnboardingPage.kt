package com.beatly.model

import androidx.annotation.DrawableRes

data class OnboardingPage(
    val title: String,
    val description: String,
    @param:DrawableRes val imageRes: Int
)