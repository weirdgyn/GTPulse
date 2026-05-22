package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.MaterialTheme

// Elegant Dark Design Theme Colors (Extracted from the spec HTML)
val ElegantDarkBackground = Color(0xFF111318) // Base layout background
val ElegantCardGrey = Color(0xFF23262B)       // Premium elevation card background
val ElegantSurfaceDark = Color(0xFF1B1B1F)    // Standard screen dark surface
val ElegantBorderGrey = Color(0xFF42474E)     // Outline contours/dividers
val ElegantPrimaryBlue = Color(0xFFD1E4FF)    // Ice blue active highlight/title/avatar border
val ElegantOnPrimaryBlue = Color(0xFF003258)  // Contrast text color for active states
val ElegantPrimaryContainer = Color(0xFF004A77) // Blue container bubble (Bulking Phase badge context)
val ElegantWarmText = Color(0xFFE2E2E6)       // Primary typography
val ElegantMutedText = Color(0xFFC4C6D0)      // Subheading typography

val ElegantSecondaryGreen = Color(0xFFB1D1B1) // Quick Stats Goal/Verified accent
val ElegantOnSecondaryGreen = Color(0xFF1B371F)
val ElegantGreenContainer = Color(0xFF374E37)  // Green background highlights

val ElegantAlertCoral = Color(0xFFFF897A)     // Heavy alert (Discard/Abort/Heavy RPE)

// Backward compatibility bindings to seamlessly skin every workout screen of ironpulse
val NeonLime: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val NeonLimeDim: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val DarkCharcoal: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val SurfaceCharcoal: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val BorderGrey: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val AccentCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondary

val AccentOrange: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.tertiary

val WarmText: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val MutedText: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val CardElevation: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == ElegantDarkBackground) Color(0x33000000) else Color(0x11000000)
