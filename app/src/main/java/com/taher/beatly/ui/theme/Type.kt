package com.taher.beatly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.taher.beatly.R

// ── Font family ────────────────────────────────────────────────────────────
// Drop inter_regular.ttf / inter_medium.ttf / inter_semibold.ttf into res/font/
val Inter = FontFamily(
    Font(R.font.inter_regular,  FontWeight.Normal),
    Font(R.font.inter_medium,   FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

// ── Named text styles (match Figma exactly) ────────────────────────────────
val DisplayLarge = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.SemiBold,
    fontSize   = 40.sp,
    lineHeight = 56.sp,
)

val Headline = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.SemiBold,
    fontSize   = 28.sp,
    lineHeight = 39.2.sp,   // 28 × 1.4 — screen titles, onboarding headlines
)

val TitleMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Bold,
    fontSize   = 22.sp,
    lineHeight = 30.8.sp,
)

val BodyMediumMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize   = 16.sp,
    lineHeight = 25.6.sp,   // field labels, button text
)

val BodyMediumRegular = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize   = 16.sp,
    lineHeight = 25.6.sp,   // paragraph text, links
)

val BodySmallRegular = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize   = 14.sp,
    lineHeight = 21.7.sp,   // input values, secondary text
)

val BodyXSmallRegular = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize   = 12.sp,
    lineHeight = 18.6.sp,   // nav bar labels, captions
)

// ── Material3 Typography mapping ───────────────────────────────────────────
val BeatlyTypography = Typography(
    displayLarge = DisplayLarge,
    titleLarge  = Headline,
    titleMedium = TitleMedium,
    bodyLarge   = BodyMediumRegular,
    labelLarge  = BodyMediumMedium,
    bodyMedium  = BodySmallRegular,
    labelMedium = BodySmallRegular,
    bodySmall   = BodyXSmallRegular,
    labelSmall  = BodyXSmallRegular,
)