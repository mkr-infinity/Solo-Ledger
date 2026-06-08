package com.solo.ledger.ui.components.cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo.ledger.ui.components.shared.AppLogo
import com.solo.ledger.ui.theme.LocalThemeDefinition
import com.solo.ledger.ui.theme.extendedColors
import com.solo.ledger.util.CurrencyFormatter

@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    currencySymbol: String,
    userName: String,
    modifier: Modifier = Modifier,
    onAnalyticsClick: () -> Unit = {}
) {
    val themeDefinition = LocalThemeDefinition.current
    val extColors = extendedColors
    val cornerRadius = themeDefinition.cornerRadiusCard

    // Animated balance counter
    var animatedBalance by remember { mutableFloatStateOf(0f) }
    val displayBalance by animateFloatAsState(
        targetValue = animatedBalance,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "balanceCounter"
    )
    LaunchedEffect(balance) { animatedBalance = balance.toFloat() }

    // 3D tilt state
    var rotX by remember { mutableFloatStateOf(-3f) }
    var rotY by remember { mutableFloatStateOf(0f) }
    val animRotX by animateFloatAsState(rotX, spring(dampingRatio = 0.5f, stiffness = 200f), label = "rotX")
    val animRotY by animateFloatAsState(rotY, spring(dampingRatio = 0.5f, stiffness = 200f), label = "rotY")

    val shadowColor = Color(0x40000000)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .drawBehind {
                for (i in 1..3) {
                    val offsetY = (i * 3).dp.toPx()
                    val alpha = (0.16f - i * 0.04f).coerceAtLeast(0f)
                    drawRoundRect(
                        color = shadowColor.copy(alpha = alpha),
                        topLeft = Offset(0f, offsetY),
                        size = size.copy(height = size.height),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                }
            }
            .graphicsLayer {
                rotationX = animRotX
                rotationY = animRotY
                cameraDistance = 12f * density
                clip = true
                shape = RoundedCornerShape(cornerRadius)
            }
            .clip(RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { rotX = -3f; rotY = 0f },
                    onDragCancel = { rotX = -3f; rotY = 0f },
                    onDrag = { change, _ ->
                        val cardW = size.width.toFloat()
                        val cardH = size.height.toFloat()
                        rotY = ((change.position.x / cardW - 0.5f) * 10f).coerceIn(-5f, 5f)
                        rotX = (-3f + (change.position.y / cardH - 0.5f) * -6f).coerceIn(-8f, 2f)
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            extColors.balanceGradientStart,
                            extColors.balanceGradientEnd
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: greeting + logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hour = java.util.Calendar.getInstance()
                        .get(java.util.Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> "Good morning"
                        hour < 17 -> "Good afternoon"
                        else      -> "Good evening"
                    }
                    Text(
                        text = "$greeting, $userName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    AppLogo(
                        size = 28.dp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Center: balance amount
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.format(displayBalance.toDouble(), currencySymbol),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = Color.White
                    )
                }

                // Bottom row: chips + analytics link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BalanceChip(
                            label = "Income",
                            amount = CurrencyFormatter.format(income, currencySymbol),
                            isIncome = true
                        )
                        BalanceChip(
                            label = "Expense",
                            amount = CurrencyFormatter.format(expense, currencySymbol),
                            isIncome = false
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onAnalyticsClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Analytics →",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceChip(
    label: String,
    amount: String,
    isIncome: Boolean
) {
    val bgColor      = if (isIncome) Color(0x3300E676) else Color(0x33FF5252)
    val contentColor = if (isIncome) Color(0xFF80FFB0) else Color(0xFFFF8A80)
    val icon         = if (isIncome) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(12.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = contentColor.copy(alpha = 0.75f)
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = contentColor
            )
        }
    }
}
