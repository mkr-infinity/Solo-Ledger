package com.solo.ledger.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NavigationStyle {
    BOTTOM_STANDARD, FLOATING_PILL, CAPSULE, TELEGRAM, MATERIAL3, COMPACT, GLASS
}

enum class CardStyle { FLAT, ELEVATED, OUTLINED, GLASS }
enum class AnimationStyle { SNAPPY, SMOOTH, BOUNCY, NONE }

data class ExtendedColors(
    val income: Color,
    val expense: Color,
    val incomeContainer: Color,
    val expenseContainer: Color,
    val chartColors: List<Color>,
    val balanceGradientStart: Color,
    val balanceGradientEnd: Color,
    val heroGradientStart: Color,
    val heroGradientEnd: Color,
    val cardHighlight: Color,
    val cardShadow: Color
)

data class ThemeDefinition(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val colorScheme: ColorScheme,
    val extendedColors: ExtendedColors,
    val cardStyle: CardStyle,
    val defaultNavStyle: NavigationStyle,
    val animationStyle: AnimationStyle,
    val cornerRadiusCard: Dp = 16.dp,
    val cornerRadiusButton: Dp = 12.dp,
    val cornerRadiusInput: Dp = 12.dp,
    val shapes: Shapes
)
