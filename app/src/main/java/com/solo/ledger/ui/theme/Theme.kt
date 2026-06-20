package com.solo.ledger.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalLedgerColors = staticCompositionLocalOf { LedgerTheme.LedgerDark.colors() }

private val LedgerTypography = Typography()

@Composable
fun SoloLedgerTheme(
    theme: LedgerTheme = LedgerTheme.LedgerDark,
    content: @Composable () -> Unit,
) {
    val ledgerColors = theme.colors()
    val colorScheme = ledgerColors.toMaterialColorScheme(isDark = theme.name.endsWith("Dark"))

    CompositionLocalProvider(LocalLedgerColors provides ledgerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LedgerTypography,
            content = content,
        )
    }
}

private fun LedgerExtendedColors.toMaterialColorScheme(isDark: Boolean): ColorScheme {
    val onAccent = if (isDark) background else Color(0xFFFFFFFF)
    val accentContainer = if (isDark) navSelected else surface
    val onAccentContainer = textPrimary
    val inverseSurface = if (isDark) textPrimary else Color(0xFF111827)
    val inverseOnSurface = if (isDark) background else Color(0xFFF8FAFC)

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onAccent,
            primaryContainer = accentContainer,
            onPrimaryContainer = onAccentContainer,
            inversePrimary = secondary,
            secondary = secondary,
            onSecondary = onAccent,
            secondaryContainer = card,
            onSecondaryContainer = textPrimary,
            tertiary = chartThree,
            onTertiary = onAccent,
            tertiaryContainer = surface,
            onTertiaryContainer = textPrimary,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = card,
            onSurfaceVariant = textSecondary,
            surfaceTint = primary,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            error = error,
            onError = Color(0xFFFFFFFF),
            errorContainer = error.copy(alpha = 0.22f),
            onErrorContainer = textPrimary,
            outline = outline,
            outlineVariant = outline,
            scrim = Color(0xCC000000),
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onAccent,
            primaryContainer = accentContainer,
            onPrimaryContainer = onAccentContainer,
            inversePrimary = secondary,
            secondary = secondary,
            onSecondary = onAccent,
            secondaryContainer = surface,
            onSecondaryContainer = textPrimary,
            tertiary = chartThree,
            onTertiary = onAccent,
            tertiaryContainer = surface,
            onTertiaryContainer = textPrimary,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = card,
            onSurfaceVariant = textSecondary,
            surfaceTint = primary,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            error = error,
            onError = Color(0xFFFFFFFF),
            errorContainer = error.copy(alpha = 0.12f),
            onErrorContainer = textPrimary,
            outline = outline,
            outlineVariant = outline,
            scrim = Color(0x99000000),
        )
    }
}
