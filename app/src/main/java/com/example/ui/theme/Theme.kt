package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DineReserveDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = GoldOnPrimary,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldOnContainer,
    secondary = CrimsonSecondary,
    onSecondary = GoldOnPrimary,
    secondaryContainer = CrimsonContainer,
    onSecondaryContainer = CrimsonOnContainer,
    tertiary = WineTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun DineReserveTheme(
    darkTheme: Boolean = true, // Default to rich dark luxury aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DineReserveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
