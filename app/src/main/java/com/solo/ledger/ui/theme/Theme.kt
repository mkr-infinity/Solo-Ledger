package com.solo.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalLedgerColors: ProvidableCompositionLocal<LedgerColors> =
    staticCompositionLocalOf { LedgerPalettes.LedgerDark }

val LocalAnimationsEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { true }

object LedgerTheme {
    val colors: LedgerColors
        @Composable get() = LocalLedgerColors.current
}

@Composable
fun SoloLedgerTheme(
    themeId: String = "ledger",
    darkMode: Boolean = isSystemInDarkTheme(),
    cornerRadius: Int = 20,
    fontScale: Float = 1f,
    highContrast: Boolean = false,
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val base = LedgerPalettes.forId(themeId, darkMode)
    val c = if (highContrast) base.copy(textSecondary = base.textPrimary, muted = base.textPrimary, outline = base.textPrimary.copy(alpha = 0.4f)) else base
    val scheme = if (darkMode) {
        darkColorScheme(
            primary = c.primary, onPrimary = c.onPrimary, secondary = c.secondary,
            background = c.background, onBackground = c.textPrimary,
            surface = c.surface, onSurface = c.textPrimary, error = c.error,
            surfaceVariant = c.card, outline = c.outline
        )
    } else {
        lightColorScheme(
            primary = c.primary, onPrimary = c.onPrimary, secondary = c.secondary,
            background = c.background, onBackground = c.textPrimary,
            surface = c.surface, onSurface = c.textPrimary, error = c.error,
            surfaceVariant = c.card, outline = c.outline
        )
    }
    CompositionLocalProvider(LocalLedgerColors provides c, LocalAnimationsEnabled provides animationsEnabled) {
        MaterialTheme(
            colorScheme = scheme,
            typography = ledgerTypography(fontScale),
            shapes = ledgerShapes(cornerRadius),
            content = content
        )
    }
}
