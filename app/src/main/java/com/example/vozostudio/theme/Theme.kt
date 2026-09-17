package com.example.vozostudio.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = VozoPrimary,
        onPrimary = Color(0xFF140726),
        primaryContainer = VozoPrimaryDark,
        onPrimaryContainer = Color.White,
        secondary = VozoSecondary,
        onSecondary = Color(0xFF002930),
        tertiary = VozoTertiary,
        onTertiary = Color.White,
        background = VozoBgDark,
        onBackground = VozoTextPrimary,
        surface = VozoBgSurface,
        onSurface = VozoTextPrimary,
        surfaceVariant = VozoBgSurfaceElevated,
        onSurfaceVariant = VozoTextSecondary,
        outline = VozoBorder,
        outlineVariant = VozoBorderLight,
        error = VozoAccentRed,
        onError = Color.White
    )

@Composable
fun VozoStudioTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
  // Always apply cohesive studio dark aesthetic
  MaterialTheme(
      colorScheme = DarkColorScheme,
      typography = Typography,
      content = content
  )
}
