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

val DarkCredTheme = ThemeDefinition(
    id = "dark_cred",
    name = "Premium Dark",
    isDark = true,
    colorScheme = darkColorScheme(
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF141414),
        surfaceVariant = Color(0xFF1E1E1E),
        primary = Color(0xFFD4AF37),
        primaryContainer = Color(0xFF2C2410),
        onPrimary = Color(0xFF000000),
        secondary = Color(0xFFC9A227),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF2A1F00),
        onSecondaryContainer = Color(0xFFFFDE9C),
        tertiary = Color(0xFFFFD700),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF332B00),
        onTertiaryContainer = Color(0xFFFFE566),
        error = Color(0xFFFF4444),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF3B0000),
        onErrorContainer = Color(0xFFFFB3B3),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFFCCCCCC),
        outline = Color(0xFF3A3A3A),
        outlineVariant = Color(0xFF2A2A2A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF5F5F5),
        inverseOnSurface = Color(0xFF141414),
        inversePrimary = Color(0xFF7A6010)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF4CAF50),
        expense = Color(0xFFFF4444),
        incomeContainer = Color(0xFF1A3B1A),
        expenseContainer = Color(0xFF3B1A1A),
        chartColors = listOf(
            Color(0xFFD4AF37),
            Color(0xFFC9A227),
            Color(0xFFFFD700),
            Color(0xFFE6B800),
            Color(0xFFB8860B),
            Color(0xFFFFC107)
        ),
        balanceGradientStart = Color(0xFF1A1500),
        balanceGradientEnd = Color(0xFF0A0A00),
        heroGradientStart = Color(0xFF2C2410),
        heroGradientEnd = Color(0xFF0A0A0A),
        cardHighlight = Color(0x1AD4AF37),
        cardShadow = Color(0xFF000000)
    ),
    cardStyle = CardStyle.ELEVATED,
    defaultNavStyle = NavigationStyle.FLOATING_PILL,
    animationStyle = AnimationStyle.SMOOTH,
    cornerRadiusCard = 20.dp,
    cornerRadiusButton = 12.dp,
    cornerRadiusInput = 12.dp,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )
)
