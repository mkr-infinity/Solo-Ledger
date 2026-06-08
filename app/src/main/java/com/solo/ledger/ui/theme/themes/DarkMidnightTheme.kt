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

val DarkMidnightTheme = ThemeDefinition(
    id = "dark_midnight",
    name = "Midnight",
    isDark = true,
    colorScheme = darkColorScheme(
        background = Color(0xFF0D1B2A),
        surface = Color(0xFF1A2744),
        surfaceVariant = Color(0xFF243354),
        primary = Color(0xFF00E5FF),
        primaryContainer = Color(0xFF002B33),
        onPrimary = Color(0xFF000000),
        secondary = Color(0xFF0288D1),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF01426A),
        onSecondaryContainer = Color(0xFFB3E5FC),
        tertiary = Color(0xFF4FC3F7),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF003C55),
        onTertiaryContainer = Color(0xFFB3E5FC),
        error = Color(0xFFFF5252),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF3B0000),
        onErrorContainer = Color(0xFFFFB3B3),
        onBackground = Color(0xFFE8F4F8),
        onSurface = Color(0xFFE8F4F8),
        onSurfaceVariant = Color(0xFFB0C8D4),
        outline = Color(0xFF2E4A60),
        outlineVariant = Color(0xFF1E3448),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE8F4F8),
        inverseOnSurface = Color(0xFF0D1B2A),
        inversePrimary = Color(0xFF006080)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF00E676),
        expense = Color(0xFFFF5252),
        incomeContainer = Color(0xFF003320),
        expenseContainer = Color(0xFF330000),
        chartColors = listOf(
            Color(0xFF00E5FF),
            Color(0xFF4FC3F7),
            Color(0xFF0288D1),
            Color(0xFF00B8D9),
            Color(0xFF26C6DA),
            Color(0xFF80DEEA)
        ),
        balanceGradientStart = Color(0xFF0D1B2A),
        balanceGradientEnd = Color(0xFF1A3A5C),
        heroGradientStart = Color(0xFF1A2744),
        heroGradientEnd = Color(0xFF0D1B2A),
        cardHighlight = Color(0x1A00E5FF),
        cardShadow = Color(0xFF000000)
    ),
    cardStyle = CardStyle.OUTLINED,
    defaultNavStyle = NavigationStyle.MATERIAL3,
    animationStyle = AnimationStyle.SNAPPY,
    cornerRadiusCard = 16.dp,
    cornerRadiusButton = 12.dp,
    cornerRadiusInput = 12.dp,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    )
)
