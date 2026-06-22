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
import androidx.compose.ui.unit.dp

val LocalLedgerColors = staticCompositionLocalOf { LedgerTheme.LedgerDark.colors() }
val LocalLedgerRadius = staticCompositionLocalOf { 28.dp }
val LocalLedgerReducedMotion = staticCompositionLocalOf { false }

private val LedgerTypography = Typography()

@Composable
fun SoloLedgerTheme(
    theme: LedgerTheme = LedgerTheme.LedgerDark,
    fontScale: Float = 1f,
    highContrast: Boolean = false,
    borderRadiusDp: Int = 28,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val baseColors = theme.colors()
    val ledgerColors = if (highContrast) {
        baseColors.copy(
            outline = baseColors.primary,
            muted = baseColors.textSecondary,
            navSelected = baseColors.primary.copy(alpha = 0.22f),
        )
    } else {
        baseColors
    }
    val colorScheme = ledgerColors.toMaterialColorScheme(isDark = theme.name.endsWith("Dark"))

    CompositionLocalProvider(
        LocalLedgerColors provides ledgerColors,
        LocalLedgerRadius provides borderRadiusDp.coerceIn(12, 40).dp,
        LocalLedgerReducedMotion provides reducedMotion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LedgerTypography.scaled(fontScale),
            content = content,
        )
    }
}

private fun Typography.scaled(scale: Float): Typography {
    val safeScale = scale.coerceIn(0.85f, 1.25f)
    return copy(
        displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize * safeScale, lineHeight = displayLarge.lineHeight * safeScale),
        displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize * safeScale, lineHeight = displayMedium.lineHeight * safeScale),
        displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize * safeScale, lineHeight = displaySmall.lineHeight * safeScale),
        headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize * safeScale, lineHeight = headlineLarge.lineHeight * safeScale),
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize * safeScale, lineHeight = headlineMedium.lineHeight * safeScale),
        headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize * safeScale, lineHeight = headlineSmall.lineHeight * safeScale),
        titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize * safeScale, lineHeight = titleLarge.lineHeight * safeScale),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * safeScale, lineHeight = titleMedium.lineHeight * safeScale),
        titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize * safeScale, lineHeight = titleSmall.lineHeight * safeScale),
        bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize * safeScale, lineHeight = bodyLarge.lineHeight * safeScale),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * safeScale, lineHeight = bodyMedium.lineHeight * safeScale),
        bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize * safeScale, lineHeight = bodySmall.lineHeight * safeScale),
        labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize * safeScale, lineHeight = labelLarge.lineHeight * safeScale),
        labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize * safeScale, lineHeight = labelMedium.lineHeight * safeScale),
        labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize * safeScale, lineHeight = labelSmall.lineHeight * safeScale),
    )
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
