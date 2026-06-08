package com.solo.ledger.ui.theme.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.AnimationStyle
import com.solo.ledger.ui.theme.CardStyle
import com.solo.ledger.ui.theme.ExtendedColors
import com.solo.ledger.ui.theme.NavigationStyle
import com.solo.ledger.ui.theme.ThemeDefinition

val LightRoseGoldTheme = ThemeDefinition(
    id = "light_rosegold",
    name = "Rose Gold",
    isDark = false,
    colorScheme = lightColorScheme(
        background = Color(0xFFFFF8F0),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFFCE4EC),
        primary = Color(0xFFC2185B),
        primaryContainer = Color(0xFFFCE4EC),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFFFD700),
        onSecondary = Color(0xFF3A2E00),
        secondaryContainer = Color(0xFFFFF8CC),
        onSecondaryContainer = Color(0xFF3A2E00),
        tertiary = Color(0xFFE91E63),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD6E4),
        onTertiaryContainer = Color(0xFF3D0021),
        error = Color(0xFFB00020),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        onBackground = Color(0xFF1A0010),
        onSurface = Color(0xFF1A0010),
        onSurfaceVariant = Color(0xFF4A1A34),
        outline = Color(0xFFE9B0C0),
        outlineVariant = Color(0xFFF4D2DD),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF1A0010),
        inverseOnSurface = Color(0xFFFFF8F0),
        inversePrimary = Color(0xFFFF80AB)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF66BB6A),
        expense = Color(0xFFC2185B),
        incomeContainer = Color(0xFFC8E6C9),
        expenseContainer = Color(0xFFFCE4EC),
        chartColors = listOf(
            Color(0xFFC2185B),
            Color(0xFFFFD700),
            Color(0xFFE91E63),
            Color(0xFFFFA726),
            Color(0xFFF48FB1),
            Color(0xFFFFCC02)
        ),
        balanceGradientStart = Color(0xFFC2185B),
        balanceGradientEnd = Color(0xFFE91E63),
        heroGradientStart = Color(0xFFFCE4EC),
        heroGradientEnd = Color(0xFFFFF8F0),
        cardHighlight = Color(0x1AC2185B),
        cardShadow = Color(0x1A000000)
    ),
    cardStyle = CardStyle.ELEVATED,
    defaultNavStyle = NavigationStyle.CAPSULE,
    animationStyle = AnimationStyle.BOUNCY,
    cornerRadiusCard = 20.dp,
    cornerRadiusButton = 20.dp,
    cornerRadiusInput = 16.dp,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )
)
