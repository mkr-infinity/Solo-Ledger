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

val LightLavenderTheme = ThemeDefinition(
    id = "light_lavender",
    name = "Lavender",
    isDark = false,
    colorScheme = lightColorScheme(
        background = Color(0xFFF8F4FF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEDE7F6),
        primary = Color(0xFF7B1FA2),
        primaryContainer = Color(0xFFEDE7F6),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF00BFA5),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFB2DFDB),
        onSecondaryContainer = Color(0xFF00251F),
        tertiary = Color(0xFFAB47BC),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFF3E5F5),
        onTertiaryContainer = Color(0xFF1A0030),
        error = Color(0xFFE53935),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        onBackground = Color(0xFF1A0030),
        onSurface = Color(0xFF1A0030),
        onSurfaceVariant = Color(0xFF4A3060),
        outline = Color(0xFFCE93D8),
        outlineVariant = Color(0xFFE1BEE7),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF1A0030),
        inverseOnSurface = Color(0xFFF8F4FF),
        inversePrimary = Color(0xFFCE93D8)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF00BFA5),
        expense = Color(0xFFE53935),
        incomeContainer = Color(0xFFB2DFDB),
        expenseContainer = Color(0xFFFFDAD6),
        chartColors = listOf(
            Color(0xFF7B1FA2),
            Color(0xFFAB47BC),
            Color(0xFF00BFA5),
            Color(0xFFCE93D8),
            Color(0xFF9C27B0),
            Color(0xFF26C6DA)
        ),
        balanceGradientStart = Color(0xFF7B1FA2),
        balanceGradientEnd = Color(0xFFAB47BC),
        heroGradientStart = Color(0xFFEDE7F6),
        heroGradientEnd = Color(0xFFF8F4FF),
        cardHighlight = Color(0x1A7B1FA2),
        cardShadow = Color(0x1A000000)
    ),
    cardStyle = CardStyle.GLASS,
    defaultNavStyle = NavigationStyle.FLOATING_PILL,
    animationStyle = AnimationStyle.SMOOTH,
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
