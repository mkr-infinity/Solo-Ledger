package com.solo.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AppTheme(val displayName: String, val isDark: Boolean, val isSquare: Boolean = false) {
    LEDGER_DARK("Ledger Dark", true),
    LEDGER_LIGHT("Ledger Light", false),
    EMERALD_DARK("Emerald Dark", true),
    EMERALD_LIGHT("Emerald Light", false),
    ANIME_DARK("Anime Dark", true),
    ANIME_LIGHT("Anime Light", false),
    SPIDER_DARK("Spider Dark", true),
    SPIDER_LIGHT("Spider Light", false),
    SQUARE_DARK("Square Dark", true, isSquare = true),
    SQUARE_LIGHT("Square Light", false, isSquare = true),
    OCEAN_DARK("Ocean Dark", true),
    OCEAN_LIGHT("Ocean Light", false),
    SUNSET_DARK("Sunset Dark", true),
    SUNSET_LIGHT("Sunset Light", false),
    MIDNIGHT("Midnight", true),
    ROSE("Rose", true),
    MINT("Mint", true);

    companion object {
        fun fromKey(key: String): AppTheme = entries.find { toKey(it) == key } ?: LEDGER_DARK

        fun toKey(theme: AppTheme): String = theme.name.lowercase()
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

private fun squareDarkColorScheme() = darkColorScheme(
    primary = SquareDarkColors.primary,
    onPrimary = SquareDarkColors.onPrimary,
    primaryContainer = SquareDarkColors.primaryContainer,
    onPrimaryContainer = SquareDarkColors.onPrimaryContainer,
    secondary = SquareDarkColors.secondary,
    onSecondary = SquareDarkColors.onSecondary,
    secondaryContainer = SquareDarkColors.secondaryContainer,
    onSecondaryContainer = SquareDarkColors.onSecondaryContainer,
    tertiary = SquareDarkColors.tertiary,
    onTertiary = SquareDarkColors.onTertiary,
    tertiaryContainer = SquareDarkColors.tertiaryContainer,
    onTertiaryContainer = SquareDarkColors.onTertiaryContainer,
    background = SquareDarkColors.background,
    onBackground = SquareDarkColors.onBackground,
    surface = SquareDarkColors.surface,
    onSurface = SquareDarkColors.onSurface,
    surfaceVariant = SquareDarkColors.surfaceVariant,
    onSurfaceVariant = SquareDarkColors.onSurfaceVariant,
    outline = SquareDarkColors.outline,
    outlineVariant = SquareDarkColors.outlineVariant,
    error = SquareDarkColors.error,
    onError = SquareDarkColors.onError,
    errorContainer = SquareDarkColors.errorContainer,
    inverseSurface = SquareDarkColors.inverseSurface,
    inverseOnSurface = SquareDarkColors.inverseOnSurface,
    scrim = SquareDarkColors.scrim
)

private fun squareLightColorScheme() = lightColorScheme(
    primary = SquareLightColors.primary,
    onPrimary = SquareLightColors.onPrimary,
    primaryContainer = SquareLightColors.primaryContainer,
    onPrimaryContainer = SquareLightColors.onPrimaryContainer,
    secondary = SquareLightColors.secondary,
    onSecondary = SquareLightColors.onSecondary,
    secondaryContainer = SquareLightColors.secondaryContainer,
    onSecondaryContainer = SquareLightColors.onSecondaryContainer,
    tertiary = SquareLightColors.tertiary,
    onTertiary = SquareLightColors.onTertiary,
    tertiaryContainer = SquareLightColors.tertiaryContainer,
    onTertiaryContainer = SquareLightColors.onTertiaryContainer,
    background = SquareLightColors.background,
    onBackground = SquareLightColors.onBackground,
    surface = SquareLightColors.surface,
    onSurface = SquareLightColors.onSurface,
    surfaceVariant = SquareLightColors.surfaceVariant,
    onSurfaceVariant = SquareLightColors.onSurfaceVariant,
    outline = SquareLightColors.outline,
    outlineVariant = SquareLightColors.outlineVariant,
    error = SquareLightColors.error,
    onError = SquareLightColors.onError,
    errorContainer = SquareLightColors.errorContainer,
    inverseSurface = SquareLightColors.inverseSurface,
    inverseOnSurface = SquareLightColors.inverseOnSurface,
    scrim = SquareLightColors.scrim
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
    AppTheme.SQUARE_DARK -> squareDarkColorScheme()
    AppTheme.SQUARE_LIGHT -> squareLightColorScheme()
    AppTheme.OCEAN_DARK -> darkColorScheme(
        primary = OceanDarkColors.primary, onPrimary = OceanDarkColors.onPrimary,
        primaryContainer = OceanDarkColors.primaryContainer, onPrimaryContainer = OceanDarkColors.onPrimaryContainer,
        secondary = OceanDarkColors.secondary, onSecondary = OceanDarkColors.onSecondary,
        secondaryContainer = OceanDarkColors.secondaryContainer, onSecondaryContainer = OceanDarkColors.onSecondaryContainer,
        tertiary = OceanDarkColors.tertiary, onTertiary = OceanDarkColors.onTertiary,
        tertiaryContainer = OceanDarkColors.tertiaryContainer, onTertiaryContainer = OceanDarkColors.onTertiaryContainer,
        background = OceanDarkColors.background, onBackground = OceanDarkColors.onBackground,
        surface = OceanDarkColors.surface, onSurface = OceanDarkColors.onSurface,
        surfaceVariant = OceanDarkColors.surfaceVariant, onSurfaceVariant = OceanDarkColors.onSurfaceVariant,
        outline = OceanDarkColors.outline, outlineVariant = OceanDarkColors.outlineVariant,
        error = OceanDarkColors.error, onError = OceanDarkColors.onError, errorContainer = OceanDarkColors.errorContainer,
        inverseSurface = OceanDarkColors.inverseSurface, inverseOnSurface = OceanDarkColors.inverseOnSurface, scrim = OceanDarkColors.scrim
    )
    AppTheme.OCEAN_LIGHT -> lightColorScheme(
        primary = OceanLightColors.primary, onPrimary = OceanLightColors.onPrimary,
        primaryContainer = OceanLightColors.primaryContainer, onPrimaryContainer = OceanLightColors.onPrimaryContainer,
        secondary = OceanLightColors.secondary, onSecondary = OceanLightColors.onSecondary,
        secondaryContainer = OceanLightColors.secondaryContainer, onSecondaryContainer = OceanLightColors.onSecondaryContainer,
        tertiary = OceanLightColors.tertiary, onTertiary = OceanLightColors.onTertiary,
        tertiaryContainer = OceanLightColors.tertiaryContainer, onTertiaryContainer = OceanLightColors.onTertiaryContainer,
        background = OceanLightColors.background, onBackground = OceanLightColors.onBackground,
        surface = OceanLightColors.surface, onSurface = OceanLightColors.onSurface,
        surfaceVariant = OceanLightColors.surfaceVariant, onSurfaceVariant = OceanLightColors.onSurfaceVariant,
        outline = OceanLightColors.outline, outlineVariant = OceanLightColors.outlineVariant,
        error = OceanLightColors.error, onError = OceanLightColors.onError, errorContainer = OceanLightColors.errorContainer,
        inverseSurface = OceanLightColors.inverseSurface, inverseOnSurface = OceanLightColors.inverseOnSurface, scrim = OceanLightColors.scrim
    )
    AppTheme.SUNSET_DARK -> darkColorScheme(
        primary = SunsetDarkColors.primary, onPrimary = SunsetDarkColors.onPrimary,
        primaryContainer = SunsetDarkColors.primaryContainer, onPrimaryContainer = SunsetDarkColors.onPrimaryContainer,
        secondary = SunsetDarkColors.secondary, onSecondary = SunsetDarkColors.onSecondary,
        secondaryContainer = SunsetDarkColors.secondaryContainer, onSecondaryContainer = SunsetDarkColors.onSecondaryContainer,
        tertiary = SunsetDarkColors.tertiary, onTertiary = SunsetDarkColors.onTertiary,
        tertiaryContainer = SunsetDarkColors.tertiaryContainer, onTertiaryContainer = SunsetDarkColors.onTertiaryContainer,
        background = SunsetDarkColors.background, onBackground = SunsetDarkColors.onBackground,
        surface = SunsetDarkColors.surface, onSurface = SunsetDarkColors.onSurface,
        surfaceVariant = SunsetDarkColors.surfaceVariant, onSurfaceVariant = SunsetDarkColors.onSurfaceVariant,
        outline = SunsetDarkColors.outline, outlineVariant = SunsetDarkColors.outlineVariant,
        error = SunsetDarkColors.error, onError = SunsetDarkColors.onError, errorContainer = SunsetDarkColors.errorContainer,
        inverseSurface = SunsetDarkColors.inverseSurface, inverseOnSurface = SunsetDarkColors.inverseOnSurface, scrim = SunsetDarkColors.scrim
    )
    AppTheme.SUNSET_LIGHT -> lightColorScheme(
        primary = SunsetLightColors.primary, onPrimary = SunsetLightColors.onPrimary,
        primaryContainer = SunsetLightColors.primaryContainer, onPrimaryContainer = SunsetLightColors.onPrimaryContainer,
        secondary = SunsetLightColors.secondary, onSecondary = SunsetLightColors.onSecondary,
        secondaryContainer = SunsetLightColors.secondaryContainer, onSecondaryContainer = SunsetLightColors.onSecondaryContainer,
        tertiary = SunsetLightColors.tertiary, onTertiary = SunsetLightColors.onTertiary,
        tertiaryContainer = SunsetLightColors.tertiaryContainer, onTertiaryContainer = SunsetLightColors.onTertiaryContainer,
        background = SunsetLightColors.background, onBackground = SunsetLightColors.onBackground,
        surface = SunsetLightColors.surface, onSurface = SunsetLightColors.onSurface,
        surfaceVariant = SunsetLightColors.surfaceVariant, onSurfaceVariant = SunsetLightColors.onSurfaceVariant,
        outline = SunsetLightColors.outline, outlineVariant = SunsetLightColors.outlineVariant,
        error = SunsetLightColors.error, onError = SunsetLightColors.onError, errorContainer = SunsetLightColors.errorContainer,
        inverseSurface = SunsetLightColors.inverseSurface, inverseOnSurface = SunsetLightColors.inverseOnSurface, scrim = SunsetLightColors.scrim
    )
    AppTheme.MIDNIGHT -> darkColorScheme(
        primary = MidnightDarkColors.primary, onPrimary = MidnightDarkColors.onPrimary,
        primaryContainer = MidnightDarkColors.primaryContainer, onPrimaryContainer = MidnightDarkColors.onPrimaryContainer,
        secondary = MidnightDarkColors.secondary, onSecondary = MidnightDarkColors.onSecondary,
        secondaryContainer = MidnightDarkColors.secondaryContainer, onSecondaryContainer = MidnightDarkColors.onSecondaryContainer,
        tertiary = MidnightDarkColors.tertiary, onTertiary = MidnightDarkColors.onTertiary,
        tertiaryContainer = MidnightDarkColors.tertiaryContainer, onTertiaryContainer = MidnightDarkColors.onTertiaryContainer,
        background = MidnightDarkColors.background, onBackground = MidnightDarkColors.onBackground,
        surface = MidnightDarkColors.surface, onSurface = MidnightDarkColors.onSurface,
        surfaceVariant = MidnightDarkColors.surfaceVariant, onSurfaceVariant = MidnightDarkColors.onSurfaceVariant,
        outline = MidnightDarkColors.outline, outlineVariant = MidnightDarkColors.outlineVariant,
        error = MidnightDarkColors.error, onError = MidnightDarkColors.onError, errorContainer = MidnightDarkColors.errorContainer,
        inverseSurface = MidnightDarkColors.inverseSurface, inverseOnSurface = MidnightDarkColors.inverseOnSurface, scrim = MidnightDarkColors.scrim
    )
    AppTheme.ROSE -> darkColorScheme(
        primary = RoseDarkColors.primary, onPrimary = RoseDarkColors.onPrimary,
        primaryContainer = RoseDarkColors.primaryContainer, onPrimaryContainer = RoseDarkColors.onPrimaryContainer,
        secondary = RoseDarkColors.secondary, onSecondary = RoseDarkColors.onSecondary,
        secondaryContainer = RoseDarkColors.secondaryContainer, onSecondaryContainer = RoseDarkColors.onSecondaryContainer,
        tertiary = RoseDarkColors.tertiary, onTertiary = RoseDarkColors.onTertiary,
        tertiaryContainer = RoseDarkColors.tertiaryContainer, onTertiaryContainer = RoseDarkColors.onTertiaryContainer,
        background = RoseDarkColors.background, onBackground = RoseDarkColors.onBackground,
        surface = RoseDarkColors.surface, onSurface = RoseDarkColors.onSurface,
        surfaceVariant = RoseDarkColors.surfaceVariant, onSurfaceVariant = RoseDarkColors.onSurfaceVariant,
        outline = RoseDarkColors.outline, outlineVariant = RoseDarkColors.outlineVariant,
        error = RoseDarkColors.error, onError = RoseDarkColors.onError, errorContainer = RoseDarkColors.errorContainer,
        inverseSurface = RoseDarkColors.inverseSurface, inverseOnSurface = RoseDarkColors.inverseOnSurface, scrim = RoseDarkColors.scrim
    )
    AppTheme.MINT -> darkColorScheme(
        primary = MintDarkColors.primary, onPrimary = MintDarkColors.onPrimary,
        primaryContainer = MintDarkColors.primaryContainer, onPrimaryContainer = MintDarkColors.onPrimaryContainer,
        secondary = MintDarkColors.secondary, onSecondary = MintDarkColors.onSecondary,
        secondaryContainer = MintDarkColors.secondaryContainer, onSecondaryContainer = MintDarkColors.onSecondaryContainer,
        tertiary = MintDarkColors.tertiary, onTertiary = MintDarkColors.onTertiary,
        tertiaryContainer = MintDarkColors.tertiaryContainer, onTertiaryContainer = MintDarkColors.onTertiaryContainer,
        background = MintDarkColors.background, onBackground = MintDarkColors.onBackground,
        surface = MintDarkColors.surface, onSurface = MintDarkColors.onSurface,
        surfaceVariant = MintDarkColors.surfaceVariant, onSurfaceVariant = MintDarkColors.onSurfaceVariant,
        outline = MintDarkColors.outline, outlineVariant = MintDarkColors.outlineVariant,
        error = MintDarkColors.error, onError = MintDarkColors.onError, errorContainer = MintDarkColors.errorContainer,
        inverseSurface = MintDarkColors.inverseSurface, inverseOnSurface = MintDarkColors.inverseOnSurface, scrim = MintDarkColors.scrim
    )
}

fun getThemeBorderRadius(theme: AppTheme): Float {
    return if (theme.isSquare) 0f else 16f
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
