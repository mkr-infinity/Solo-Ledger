package com.solo.ledger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun ledgerTypography(scale: Float = 1f): Typography {
    val f = FontFamily.Default
    fun sp(v: Int) = (v * scale).sp
    return Typography(
        displaySmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = sp(34), letterSpacing = (-0.5).sp),
        headlineMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = sp(26)),
        headlineSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = sp(22)),
        titleLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = sp(20)),
        titleMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = sp(16)),
        bodyLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = sp(16)),
        bodyMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = sp(14)),
        labelLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = sp(14)),
        labelMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = sp(12))
    )
}
