package com.solo.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String, val isDark: Boolean) {
    LEDGER_DARK("Ledger Dark", true),
    LEDGER_LIGHT("Ledger Light", false),
    EMERALD_DARK("Emerald Dark", true),
    EMERALD_LIGHT("Emerald Light", false),
    ANIME_DARK("Anime Dark", true),
    ANIME_LIGHT("Anime Light", false),
    SPIDER_DARK("Spider Dark", true),
    SPIDER_LIGHT("Spider Light", false);

    companion object {
        fun fromKey(key: String): AppTheme = when (key) {
            "ledger_dark" -> LEDGER_DARK
            "ledger_light" -> LEDGER_LIGHT
            "emerald_dark" -> EMERALD_DARK
            "emerald_light" -> EMERALD_LIGHT
            "anime_dark" -> ANIME_DARK
            "anime_light" -> ANIME_LIGHT
            "spider_dark" -> SPIDER_DARK
            "spider_light" -> SPIDER_LIGHT
            else -> LEDGER_DARK
        }

        fun toKey(theme: AppTheme): String = when (theme) {
            LEDGER_DARK -> "ledger_dark"
            LEDGER_LIGHT -> "ledger_light"
            EMERALD_DARK -> "emerald_dark"
            EMERALD_LIGHT -> "emerald_light"
            ANIME_DARK -> "anime_dark"
            ANIME_LIGHT -> "anime_light"
            SPIDER_DARK -> "spider_dark"
            SPIDER_LIGHT -> "spider_light"
        }
    }
}

private fun ledgerDarkColorScheme() = darkColorScheme(
    primary = LedgerDarkColors.primary,
    onPrimary = LedgerDarkColors.onPrimary,
    primaryContainer = LedgerDarkColors.primaryContainer,
    onPrimaryContainer = LedgerDarkColors.onPrimaryContainer,
    secondary = LedgerDarkColors.secondary,
    onSecondary = LedgerDarkColors.onSecondary,
    secondaryContainer = LedgerDarkColors.secondaryContainer,
    onSecondaryContainer = LedgerDarkColors.onSecondaryContainer,
    tertiary = LedgerDarkColors.tertiary,
    onTertiary = LedgerDarkColors.onTertiary,
    tertiaryContainer = LedgerDarkColors.tertiaryContainer,
    onTertiaryContainer = LedgerDarkColors.onTertiaryContainer,
    background = LedgerDarkColors.background,
    onBackground = LedgerDarkColors.onBackground,
    surface = LedgerDarkColors.surface,
    onSurface = LedgerDarkColors.onSurface,
    surfaceVariant = LedgerDarkColors.surfaceVariant,
    onSurfaceVariant = LedgerDarkColors.onSurfaceVariant,
    outline = LedgerDarkColors.outline,
    outlineVariant = LedgerDarkColors.outlineVariant,
    error = LedgerDarkColors.error,
    onError = LedgerDarkColors.onError,
    errorContainer = LedgerDarkColors.errorContainer,
    inverseSurface = LedgerDarkColors.inverseSurface,
    inverseOnSurface = LedgerDarkColors.inverseOnSurface,
    scrim = LedgerDarkColors.scrim
)

private fun ledgerLightColorScheme() = lightColorScheme(
    primary = LedgerLightColors.primary,
    onPrimary = LedgerLightColors.onPrimary,
    primaryContainer = LedgerLightColors.primaryContainer,
    onPrimaryContainer = LedgerLightColors.onPrimaryContainer,
    secondary = LedgerLightColors.secondary,
    onSecondary = LedgerLightColors.onSecondary,
    secondaryContainer = LedgerLightColors.secondaryContainer,
    onSecondaryContainer = LedgerLightColors.onSecondaryContainer,
    tertiary = LedgerLightColors.tertiary,
    onTertiary = LedgerLightColors.onTertiary,
    tertiaryContainer = LedgerLightColors.tertiaryContainer,
    onTertiaryContainer = LedgerLightColors.onTertiaryContainer,
    background = LedgerLightColors.background,
    onBackground = LedgerLightColors.onBackground,
    surface = LedgerLightColors.surface,
    onSurface = LedgerLightColors.onSurface,
    surfaceVariant = LedgerLightColors.surfaceVariant,
    onSurfaceVariant = LedgerLightColors.onSurfaceVariant,
    outline = LedgerLightColors.outline,
    outlineVariant = LedgerLightColors.outlineVariant,
    error = LedgerLightColors.error,
    onError = LedgerLightColors.onError,
    errorContainer = LedgerLightColors.errorContainer,
    inverseSurface = LedgerLightColors.inverseSurface,
    inverseOnSurface = LedgerLightColors.inverseOnSurface,
    scrim = LedgerLightColors.scrim
)

private fun emeraldDarkColorScheme() = darkColorScheme(
    primary = EmeraldDarkColors.primary,
    onPrimary = EmeraldDarkColors.onPrimary,
    primaryContainer = EmeraldDarkColors.primaryContainer,
    onPrimaryContainer = EmeraldDarkColors.onPrimaryContainer,
    secondary = EmeraldDarkColors.secondary,
    onSecondary = EmeraldDarkColors.onSecondary,
    secondaryContainer = EmeraldDarkColors.secondaryContainer,
    onSecondaryContainer = EmeraldDarkColors.onSecondaryContainer,
    tertiary = EmeraldDarkColors.tertiary,
    onTertiary = EmeraldDarkColors.onTertiary,
    tertiaryContainer = EmeraldDarkColors.tertiaryContainer,
    onTertiaryContainer = EmeraldDarkColors.onTertiaryContainer,
    background = EmeraldDarkColors.background,
    onBackground = EmeraldDarkColors.onBackground,
    surface = EmeraldDarkColors.surface,
    onSurface = EmeraldDarkColors.onSurface,
    surfaceVariant = EmeraldDarkColors.surfaceVariant,
    onSurfaceVariant = EmeraldDarkColors.onSurfaceVariant,
    outline = EmeraldDarkColors.outline,
    outlineVariant = EmeraldDarkColors.outlineVariant,
    error = EmeraldDarkColors.error,
    onError = EmeraldDarkColors.onError,
    errorContainer = EmeraldDarkColors.errorContainer,
    inverseSurface = EmeraldDarkColors.inverseSurface,
    inverseOnSurface = EmeraldDarkColors.inverseOnSurface,
    scrim = EmeraldDarkColors.scrim
)

private fun emeraldLightColorScheme() = lightColorScheme(
    primary = EmeraldLightColors.primary,
    onPrimary = EmeraldLightColors.onPrimary,
    primaryContainer = EmeraldLightColors.primaryContainer,
    onPrimaryContainer = EmeraldLightColors.onPrimaryContainer,
    secondary = EmeraldLightColors.secondary,
    onSecondary = EmeraldLightColors.onSecondary,
    secondaryContainer = EmeraldLightColors.secondaryContainer,
    onSecondaryContainer = EmeraldLightColors.onSecondaryContainer,
    tertiary = EmeraldLightColors.tertiary,
    onTertiary = EmeraldLightColors.onTertiary,
    tertiaryContainer = EmeraldLightColors.tertiaryContainer,
    onTertiaryContainer = EmeraldLightColors.onTertiaryContainer,
    background = EmeraldLightColors.background,
    onBackground = EmeraldLightColors.onBackground,
    surface = EmeraldLightColors.surface,
    onSurface = EmeraldLightColors.onSurface,
    surfaceVariant = EmeraldLightColors.surfaceVariant,
    onSurfaceVariant = EmeraldLightColors.onSurfaceVariant,
    outline = EmeraldLightColors.outline,
    outlineVariant = EmeraldLightColors.outlineVariant,
    error = EmeraldLightColors.error,
    onError = EmeraldLightColors.onError,
    errorContainer = EmeraldLightColors.errorContainer,
    inverseSurface = EmeraldLightColors.inverseSurface,
    inverseOnSurface = EmeraldLightColors.inverseOnSurface,
    scrim = EmeraldLightColors.scrim
)

private fun animeDarkColorScheme() = darkColorScheme(
    primary = AnimeDarkColors.primary,
    onPrimary = AnimeDarkColors.onPrimary,
    primaryContainer = AnimeDarkColors.primaryContainer,
    onPrimaryContainer = AnimeDarkColors.onPrimaryContainer,
    secondary = AnimeDarkColors.secondary,
    onSecondary = AnimeDarkColors.onSecondary,
    secondaryContainer = AnimeDarkColors.secondaryContainer,
    onSecondaryContainer = AnimeDarkColors.onSecondaryContainer,
    tertiary = AnimeDarkColors.tertiary,
    onTertiary = AnimeDarkColors.onTertiary,
    tertiaryContainer = AnimeDarkColors.tertiaryContainer,
    onTertiaryContainer = AnimeDarkColors.onTertiaryContainer,
    background = AnimeDarkColors.background,
    onBackground = AnimeDarkColors.onBackground,
    surface = AnimeDarkColors.surface,
    onSurface = AnimeDarkColors.onSurface,
    surfaceVariant = AnimeDarkColors.surfaceVariant,
    onSurfaceVariant = AnimeDarkColors.onSurfaceVariant,
    outline = AnimeDarkColors.outline,
    outlineVariant = AnimeDarkColors.outlineVariant,
    error = AnimeDarkColors.error,
    onError = AnimeDarkColors.onError,
    errorContainer = AnimeDarkColors.errorContainer,
    inverseSurface = AnimeDarkColors.inverseSurface,
    inverseOnSurface = AnimeDarkColors.inverseOnSurface,
    scrim = AnimeDarkColors.scrim
)

private fun animeLightColorScheme() = lightColorScheme(
    primary = AnimeLightColors.primary,
    onPrimary = AnimeLightColors.onPrimary,
    primaryContainer = AnimeLightColors.primaryContainer,
    onPrimaryContainer = AnimeLightColors.onPrimaryContainer,
    secondary = AnimeLightColors.secondary,
    onSecondary = AnimeLightColors.onSecondary,
    secondaryContainer = AnimeLightColors.secondaryContainer,
    onSecondaryContainer = AnimeLightColors.onSecondaryContainer,
    tertiary = AnimeLightColors.tertiary,
    onTertiary = AnimeLightColors.onTertiary,
    tertiaryContainer = AnimeLightColors.tertiaryContainer,
    onTertiaryContainer = AnimeLightColors.onTertiaryContainer,
    background = AnimeLightColors.background,
    onBackground = AnimeLightColors.onBackground,
    surface = AnimeLightColors.surface,
    onSurface = AnimeLightColors.onSurface,
    surfaceVariant = AnimeLightColors.surfaceVariant,
    onSurfaceVariant = AnimeLightColors.onSurfaceVariant,
    outline = AnimeLightColors.outline,
    outlineVariant = AnimeLightColors.outlineVariant,
    error = AnimeLightColors.error,
    onError = AnimeLightColors.onError,
    errorContainer = AnimeLightColors.errorContainer,
    inverseSurface = AnimeLightColors.inverseSurface,
    inverseOnSurface = AnimeLightColors.inverseOnSurface,
    scrim = AnimeLightColors.scrim
)

private fun spiderDarkColorScheme() = darkColorScheme(
    primary = SpiderDarkColors.primary,
    onPrimary = SpiderDarkColors.onPrimary,
    primaryContainer = SpiderDarkColors.primaryContainer,
    onPrimaryContainer = SpiderDarkColors.onPrimaryContainer,
    secondary = SpiderDarkColors.secondary,
    onSecondary = SpiderDarkColors.onSecondary,
    secondaryContainer = SpiderDarkColors.secondaryContainer,
    onSecondaryContainer = SpiderDarkColors.onSecondaryContainer,
    tertiary = SpiderDarkColors.tertiary,
    onTertiary = SpiderDarkColors.onTertiary,
    tertiaryContainer = SpiderDarkColors.tertiaryContainer,
    onTertiaryContainer = SpiderDarkColors.onTertiaryContainer,
    background = SpiderDarkColors.background,
    onBackground = SpiderDarkColors.onBackground,
    surface = SpiderDarkColors.surface,
    onSurface = SpiderDarkColors.onSurface,
    surfaceVariant = SpiderDarkColors.surfaceVariant,
    onSurfaceVariant = SpiderDarkColors.onSurfaceVariant,
    outline = SpiderDarkColors.outline,
    outlineVariant = SpiderDarkColors.outlineVariant,
    error = SpiderDarkColors.error,
    onError = SpiderDarkColors.onError,
    errorContainer = SpiderDarkColors.errorContainer,
    inverseSurface = SpiderDarkColors.inverseSurface,
    inverseOnSurface = SpiderDarkColors.inverseOnSurface,
    scrim = SpiderDarkColors.scrim
)

private fun spiderLightColorScheme() = lightColorScheme(
    primary = SpiderLightColors.primary,
    onPrimary = SpiderLightColors.onPrimary,
    primaryContainer = SpiderLightColors.primaryContainer,
    onPrimaryContainer = SpiderLightColors.onPrimaryContainer,
    secondary = SpiderLightColors.secondary,
    onSecondary = SpiderLightColors.onSecondary,
    secondaryContainer = SpiderLightColors.secondaryContainer,
    onSecondaryContainer = SpiderLightColors.onSecondaryContainer,
    tertiary = SpiderLightColors.tertiary,
    onTertiary = SpiderLightColors.onTertiary,
    tertiaryContainer = SpiderLightColors.tertiaryContainer,
    onTertiaryContainer = SpiderLightColors.onTertiaryContainer,
    background = SpiderLightColors.background,
    onBackground = SpiderLightColors.onBackground,
    surface = SpiderLightColors.surface,
    onSurface = SpiderLightColors.onSurface,
    surfaceVariant = SpiderLightColors.surfaceVariant,
    onSurfaceVariant = SpiderLightColors.onSurfaceVariant,
    outline = SpiderLightColors.outline,
    outlineVariant = SpiderLightColors.outlineVariant,
    error = SpiderLightColors.error,
    onError = SpiderLightColors.onError,
    errorContainer = SpiderLightColors.errorContainer,
    inverseSurface = SpiderLightColors.inverseSurface,
    inverseOnSurface = SpiderLightColors.inverseOnSurface,
    scrim = SpiderLightColors.scrim
)

fun getColorScheme(theme: AppTheme): ColorScheme = when (theme) {
    AppTheme.LEDGER_DARK -> ledgerDarkColorScheme()
    AppTheme.LEDGER_LIGHT -> ledgerLightColorScheme()
    AppTheme.EMERALD_DARK -> emeraldDarkColorScheme()
    AppTheme.EMERALD_LIGHT -> emeraldLightColorScheme()
    AppTheme.ANIME_DARK -> animeDarkColorScheme()
    AppTheme.ANIME_LIGHT -> animeLightColorScheme()
    AppTheme.SPIDER_DARK -> spiderDarkColorScheme()
    AppTheme.SPIDER_LIGHT -> spiderLightColorScheme()
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.LEDGER_DARK }

@Composable
fun SoloLedgerTheme(
    appTheme: AppTheme = AppTheme.LEDGER_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(appTheme)

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SoloLedgerTypography,
            content = content
        )
    }
}
