package com.solo.ledger.ui.theme.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.AnimationStyle
import com.solo.ledger.ui.theme.CardStyle
import com.solo.ledger.ui.theme.ExtendedColors
import com.solo.ledger.ui.theme.NavigationStyle
import com.solo.ledger.ui.theme.ThemeDefinition

val DarkObsidianTheme = ThemeDefinition(
    id = "dark_obsidian",
    name = "Obsidian",
    isDark = true,
    colorScheme = darkColorScheme(
        background = Color(0xFF000000),
        surface = Color(0xFF0A0A0A),
        surfaceVariant = Color(0xFF111111),
        primary = Color(0xFF39FF14),
        primaryContainer = Color(0xFF001A00),
        onPrimary = Color(0xFF000000),
        secondary = Color(0xFF00FF41),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF002200),
        onSecondaryContainer = Color(0xFF00FF41),
        tertiary = Color(0xFF7FFF00),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF1A2E00),
        onTertiaryContainer = Color(0xFF7FFF00),
        error = Color(0xFFFF0040),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF2D0010),
        onErrorContainer = Color(0xFFFF8099),
        onBackground = Color(0xFFCCCCCC),
        onSurface = Color(0xFFCCCCCC),
        onSurfaceVariant = Color(0xFF999999),
        outline = Color(0xFF222222),
        outlineVariant = Color(0xFF1A1A1A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFCCCCCC),
        inverseOnSurface = Color(0xFF0A0A0A),
        inversePrimary = Color(0xFF006600)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF39FF14),
        expense = Color(0xFFFF0040),
        incomeContainer = Color(0xFF001A00),
        expenseContainer = Color(0xFF2D0010),
        chartColors = listOf(
            Color(0xFF39FF14),
            Color(0xFF7FFF00),
            Color(0xFFADFF2F),
            Color(0xFF00FF41),
            Color(0xFF76FF03),
            Color(0xFFCCFF33)
        ),
        balanceGradientStart = Color(0xFF001A00),
        balanceGradientEnd = Color(0xFF000000),
        heroGradientStart = Color(0xFF0A0A0A),
        heroGradientEnd = Color(0xFF000000),
        cardHighlight = Color(0x1A39FF14),
        cardShadow = Color(0xFF000000)
    ),
    cardStyle = CardStyle.FLAT,
    defaultNavStyle = NavigationStyle.COMPACT,
    animationStyle = AnimationStyle.SNAPPY,
    cornerRadiusCard = 4.dp,
    cornerRadiusButton = 4.dp,
    cornerRadiusInput = 4.dp,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(2.dp),
        small = RoundedCornerShape(2.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(4.dp),
        extraLarge = RoundedCornerShape(4.dp)
    )
)
