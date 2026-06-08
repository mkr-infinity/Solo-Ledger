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

val DarkNebulaTheme = ThemeDefinition(
    id = "dark_nebula",
    name = "Nebula",
    isDark = true,
    colorScheme = darkColorScheme(
        background = Color(0xFF1A0A2E),
        surface = Color(0xFF24103E),
        surfaceVariant = Color(0xFF30165C),
        primary = Color(0xFFFF2EAF),
        primaryContainer = Color(0xFF300020),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF9C27B0),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF4A0060),
        onSecondaryContainer = Color(0xFFE1BEE7),
        tertiary = Color(0xFFE040FB),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF4A0060),
        onTertiaryContainer = Color(0xFFF3E5F5),
        error = Color(0xFFFF6B6B),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF3B0000),
        onErrorContainer = Color(0xFFFFB3B3),
        onBackground = Color(0xFFF0E6FF),
        onSurface = Color(0xFFF0E6FF),
        onSurfaceVariant = Color(0xFFCCAAFF),
        outline = Color(0xFF4A2070),
        outlineVariant = Color(0xFF36145A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFF0E6FF),
        inverseOnSurface = Color(0xFF1A0A2E),
        inversePrimary = Color(0xFF880040)
    ),
    extendedColors = ExtendedColors(
        income = Color(0xFF00E676),
        expense = Color(0xFFFF2EAF),
        incomeContainer = Color(0xFF003320),
        expenseContainer = Color(0xFF300020),
        chartColors = listOf(
            Color(0xFFFF2EAF),
            Color(0xFF9C27B0),
            Color(0xFFE040FB),
            Color(0xFFCE93D8),
            Color(0xFFAB47BC),
            Color(0xFFFF80CC)
        ),
        balanceGradientStart = Color(0xFF1A0A2E),
        balanceGradientEnd = Color(0xFF300050),
        heroGradientStart = Color(0xFF24103E),
        heroGradientEnd = Color(0xFF1A0A2E),
        cardHighlight = Color(0x1AFF2EAF),
        cardShadow = Color(0xFF000000)
    ),
    cardStyle = CardStyle.GLASS,
    defaultNavStyle = NavigationStyle.CAPSULE,
    animationStyle = AnimationStyle.BOUNCY,
    cornerRadiusCard = 24.dp,
    cornerRadiusButton = 24.dp,
    cornerRadiusInput = 16.dp,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )
)
