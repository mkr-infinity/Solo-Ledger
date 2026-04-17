package com.kaif.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    MIDNIGHT,
    TWILIGHT,
    AURORA,
    NOON
}

private val MidnightColorScheme = darkColorScheme(
    primary = MidnightTheme.Primary,
    onPrimary = MidnightTheme.OnPrimary,
    secondary = MidnightTheme.Secondary,
    onSecondary = MidnightTheme.OnSecondary,
    background = MidnightTheme.Background,
    onBackground = MidnightTheme.OnBackground,
    surface = MidnightTheme.Surface,
    onSurface = MidnightTheme.OnSurface,
    error = MidnightTheme.Error,
    tertiary = MidnightTheme.Tertiary,
    onTertiary = Color.White
)

private val TwilightColorScheme = darkColorScheme(
    primary = TwilightTheme.Primary,
    onPrimary = TwilightTheme.OnPrimary,
    secondary = TwilightTheme.Secondary,
    onSecondary = TwilightTheme.OnSecondary,
    background = TwilightTheme.Background,
    onBackground = TwilightTheme.OnBackground,
    surface = TwilightTheme.Surface,
    onSurface = TwilightTheme.OnSurface,
    error = TwilightTheme.Error,
    tertiary = TwilightTheme.Tertiary,
    onTertiary = Color.White
)

private val AuroraColorScheme = lightColorScheme(
    primary = AuroraTheme.Primary,
    onPrimary = AuroraTheme.OnPrimary,
    secondary = AuroraTheme.Secondary,
    onSecondary = AuroraTheme.OnSecondary,
    background = AuroraTheme.Background,
    onBackground = AuroraTheme.OnBackground,
    surface = AuroraTheme.Surface,
    onSurface = AuroraTheme.OnSurface,
    error = AuroraTheme.Error,
    tertiary = AuroraTheme.Tertiary,
    onTertiary = Color.White
)

private val NoonColorScheme = lightColorScheme(
    primary = NoonTheme.Primary,
    onPrimary = NoonTheme.OnPrimary,
    secondary = NoonTheme.Secondary,
    onSecondary = NoonTheme.OnSecondary,
    background = NoonTheme.Background,
    onBackground = NoonTheme.OnBackground,
    surface = NoonTheme.Surface,
    onSurface = NoonTheme.OnSurface,
    error = NoonTheme.Error,
    tertiary = NoonTheme.Tertiary,
    onTertiary = Color.White
)

@Composable
fun SoloLedgerTheme(
    themeType: ThemeType = ThemeType.MIDNIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.MIDNIGHT -> MidnightColorScheme
        ThemeType.TWILIGHT -> TwilightColorScheme
        ThemeType.AURORA -> AuroraColorScheme
        ThemeType.NOON -> NoonColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SoloTypography,
        shapes = SoloShapes,
        content = content
    )
}

// Helper function to get theme name
fun ThemeType.getDisplayName(): String = when (this) {
    ThemeType.MIDNIGHT -> "Midnight"
    ThemeType.TWILIGHT -> "Twilight"
    ThemeType.AURORA -> "Aurora"
    ThemeType.NOON -> "Noon"
}

fun String.toThemeType(): ThemeType = when (this) {
    "MIDNIGHT" -> ThemeType.MIDNIGHT
    "TWILIGHT" -> ThemeType.TWILIGHT
    "AURORA" -> ThemeType.AURORA
    "NOON" -> ThemeType.NOON
    else -> ThemeType.MIDNIGHT
}
