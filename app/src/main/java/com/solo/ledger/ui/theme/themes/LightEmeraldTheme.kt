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

val LightEmeraldTheme = ThemeDefinition(
    id = "light_emerald",
    name = "Emerald",
    isDark = false,
    colorScheme = lightColorScheme(
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF5FFF8),
        surfaceVariant = Color(0xFFE8F5E9),
        primary = Color(0xFF00C853),
        primaryContainer = Color(0xFFCCFFE0),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF00897B),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFB2DFDB),
        onSecondaryContainer = Color(0xFF00251F),
        tertiary = Color(0xFF43A047),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFC8E6C9),
        onTertiaryContainer = Color(0xFF002204),
        error = Color(0xFFF44336),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        onBackground = Color(0xFF1A1A1A),
        onSurface = Color(0xFF212121),
        onSurfaceVariant = Color(0xFF424242),
        outline = Color(0xFFBDBDBD),
        outlineVariant = Color(0xFFE0E0E0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF212121),
        inverseOnSurface = Color(0xFFF5FFF8),
        inversePrimary = Color(0xFF4CAF50)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF00C853),
        expense = Color(0xFFF44336),
        incomeContainer = Color(0xFFCCFFE0),
        expenseContainer = Color(0xFFFFDAD6),
        chartColors = listOf(
            Color(0xFF00C853),
            Color(0xFF43A047),
            Color(0xFF00897B),
            Color(0xFF26A69A),
            Color(0xFF66BB6A),
            Color(0xFF00BFA5)
        ),
        balanceGradientStart = Color(0xFF00C853),
        balanceGradientEnd = Color(0xFF00897B),
        heroGradientStart = Color(0xFFE8F5E9),
        heroGradientEnd = Color(0xFFF5FFF8),
        cardHighlight = Color(0x1A00C853),
        cardShadow = Color(0x1A000000)
    ),
    cardStyle = CardStyle.FLAT,
    defaultNavStyle = NavigationStyle.BOTTOM_STANDARD,
    animationStyle = AnimationStyle.SMOOTH,
    cornerRadiusCard = 12.dp,
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
