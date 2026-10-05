package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BismaDarkColorScheme = darkColorScheme(
    primary = NeonPurpleLight,
    onPrimary = Color.White,
    primaryContainer = RoyalVioletCard,
    onPrimaryContainer = TextPrimary,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = RoyalVioletCardAlt,
    onSecondaryContainer = GoldAccent,
    tertiary = NeonCyan,
    onTertiary = Color.Black,
    background = RoyalVioletDark,
    onBackground = TextPrimary,
    surface = RoyalVioletSurface,
    onSurface = TextPrimary,
    surfaceVariant = RoyalVioletCard,
    onSurfaceVariant = TextSecondary,
    outline = RoyalVioletBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BismaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
