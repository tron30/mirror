package com.mirrorx.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mirrorx.data.AppTheme

private val LightScheme = lightColorScheme(
    primary = MirrorTeal,
    onPrimary = MirrorBlack,
    secondary = MirrorGold,
    onSecondary = MirrorBlack,
    tertiary = MirrorCoral,
    background = MirrorBlack,
    onBackground = MirrorWhite,
    surface = Color.White.copy(alpha = 0.16f),
    onSurface = MirrorWhite,
    surfaceVariant = Color.White.copy(alpha = 0.12f),
    onSurfaceVariant = Color.White.copy(alpha = 0.82f),
    outline = Color.White.copy(alpha = 0.24f),
)

private val DarkScheme = darkColorScheme(
    primary = MirrorTeal,
    onPrimary = MirrorBlack,
    secondary = MirrorGold,
    onSecondary = MirrorBlack,
    tertiary = MirrorCoral,
    background = MirrorBlack,
    onBackground = MirrorWhite,
    surface = MirrorInk.copy(alpha = 0.82f),
    onSurface = MirrorWhite,
    surfaceVariant = Color(0xFF202020).copy(alpha = 0.88f),
    onSurfaceVariant = Color.White.copy(alpha = 0.78f),
    outline = Color.White.copy(alpha = 0.2f),
)

@Composable
fun MirrorXTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (appTheme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = MirrorTypography,
        content = content,
    )
}
