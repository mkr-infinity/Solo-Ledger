package com.solo.ledger.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

fun ledgerShapes(radius: Int = 20) = Shapes(
    extraSmall = RoundedCornerShape((radius / 2.5).dp),
    small = RoundedCornerShape((radius / 1.6).dp),
    medium = RoundedCornerShape(radius.dp),
    large = RoundedCornerShape((radius * 1.3).dp),
    extraLarge = RoundedCornerShape((radius * 1.6).dp)
)
