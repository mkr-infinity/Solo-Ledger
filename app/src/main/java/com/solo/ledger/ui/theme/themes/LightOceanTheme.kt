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

val LightOceanTheme = ThemeDefinition(
    id = "light_ocean",
    name = "Ocean",
    isDark = false,
    colorScheme = lightColorScheme(
        background = Color(0xFFF0F9FF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE1F5FE),
        primary = Color(0xFF0288D1),
        primaryContainer = Color(0xFFB3E5FC),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFFF7043),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFCCBC),
        onSecondaryContainer = Color(0xFF3E0E00),
        tertiary = Color(0xFF26C6DA),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFB2EBF2),
        onTertiaryContainer = Color(0xFF00252A),
        error = Color(0xFFEF5350),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        onBackground = Color(0xFF0D2137),
        onSurface = Color(0xFF0D2137),
        onSurfaceVariant = Color(0xFF2A4A63),
        outline = Color(0xFF90CAF9),
        outlineVariant = Color(0xFFBBDEFB),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF0D2137),
        inverseOnSurface = Color(0xFFF0F9FF),
        inversePrimary = Color(0xFF81D4FA)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF26A69A),
        expense = Color(0xFFEF5350),
        incomeContainer = Color(0xFFB2DFDB),
        expenseContainer = Color(0xFFFFDAD6),
        chartColors = listOf(
            Color(0xFF0288D1),
            Color(0xFFFF7043),
            Color(0xFF26C6DA),
            Color(0xFF4FC3F7),
            Color(0xFF0097A7),
            Color(0xFF80DEEA)
        ),
        balanceGradientStart = Color(0xFF0288D1),
        balanceGradientEnd = Color(0xFF0097A7),
        heroGradientStart = Color(0xFFE1F5FE),
        heroGradientEnd = Color(0xFFF0F9FF),
        cardHighlight = Color(0x1A0288D1),
        cardShadow = Color(0x1A000000)
    ),
    cardStyle = CardStyle.ELEVATED,
    defaultNavStyle = NavigationStyle.MATERIAL3,
    animationStyle = AnimationStyle.SMOOTH,
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
