package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElegantDarkColorScheme = darkColorScheme(
    primary = ElegantPrimaryBlue,
    onPrimary = ElegantOnPrimaryBlue,
    primaryContainer = ElegantPrimaryContainer,
    onPrimaryContainer = ElegantPrimaryBlue,
    secondary = ElegantSecondaryGreen,
    onSecondary = ElegantOnSecondaryGreen,
    tertiary = ElegantAlertCoral,
    background = ElegantDarkBackground,
    surface = ElegantCardGrey,
    onBackground = ElegantWarmText,
    onSurface = ElegantWarmText,
    surfaceVariant = ElegantSurfaceDark,
    onSurfaceVariant = ElegantMutedText,
    outline = ElegantBorderGrey
)

private val ElegantLightColorScheme = lightColorScheme(
    primary = Color(0xFF005FAF),           // Elite Blue accent
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF1B6C3B),         // Stats Green accent
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F2D1),
    tertiary = Color(0xFFBA1A1A),           // Crimson alert
    background = Color(0xFFF8F9FC),        // Base light layout background
    surface = Color(0xFFFFFFFF),           // Content elevation card background (pure white)
    onBackground = Color(0xFF111318),      // Base dark onyx text
    onSurface = Color(0xFF111318),         // Onyx text for cards
    surfaceVariant = Color(0xFFE2E2E6),    // Alternative light surface
    onSurfaceVariant = Color(0xFF42474E),  // Muted gray/slate typography
    outline = Color(0xFFC4C6D0)            // Soft contour grey
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Centralized theme selection support
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) ElegantDarkColorScheme else ElegantLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
