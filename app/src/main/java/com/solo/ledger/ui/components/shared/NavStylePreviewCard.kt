package com.solo.ledger.ui.components.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo.ledger.ui.theme.NavigationStyle

@Composable
fun NavStylePreviewCard(
    style: NavigationStyle,
    styleName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.95f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "navCardScale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val borderWidth = if (isSelected) 2.5.dp else 1.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Box {
            Card(
                modifier = Modifier
                    .size(width = 120.dp, height = 80.dp)
                    .clickable(
                        indication = ripple(bounded = true),
                        interactionSource = null,
                        onClick = onClick
                    ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(borderWidth, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Canvas(modifier = Modifier.size(width = 120.dp, height = 80.dp)) {
                    val w = size.width
                    val h = size.height
                    val barH = 14.dp.toPx()

                    when (style) {
                        NavigationStyle.BOTTOM_STANDARD -> {
                            // Bar at bottom
                            drawRect(
                                color = surfaceColor,
                                topLeft = Offset(0f, h - barH),
                                size = Size(w, barH)
                            )
                            // 5 dots evenly spaced
                            val step = w / 6f
                            for (i in 1..5) {
                                val isActive = i == 3
                                drawCircle(
                                    color = if (isActive) primaryColor else onSurfaceColor.copy(alpha = 0.35f),
                                    radius = if (isActive) 4.dp.toPx() else 3.dp.toPx(),
                                    center = Offset(step * i, h - barH / 2f)
                                )
                            }
                        }

                        NavigationStyle.FLOATING_PILL -> {
                            val pillW = w * 0.55f
                            val pillH = barH * 1.1f
                            val pillX = (w - pillW) / 2f
                            val pillY = h - pillH - 6.dp.toPx()
                            drawRoundRect(
                                color = surfaceColor,
                                topLeft = Offset(pillX, pillY),
                                size = Size(pillW, pillH),
                                cornerRadius = CornerRadius(pillH / 2f)
                            )
                            val step = pillW / 4f
                            for (i in 1..3) {
                                drawCircle(
                                    color = if (i == 2) primaryColor else onSurfaceColor.copy(alpha = 0.4f),
                                    radius = 3.dp.toPx(),
                                    center = Offset(pillX + step * i, pillY + pillH / 2f)
                                )
                            }
                        }

                        NavigationStyle.CAPSULE -> {
                            val capH = barH
                            val capMargin = 8.dp.toPx()
                            drawRoundRect(
                                color = surfaceColor,
                                topLeft = Offset(capMargin, h - capH - 4.dp.toPx()),
                                size = Size(w - capMargin * 2, capH),
                                cornerRadius = CornerRadius(capH / 2f)
                            )
                            val step = (w - capMargin * 2) / 6f
                            for (i in 1..5) {
                                drawCircle(
                                    color = if (i == 3) primaryColor else onSurfaceColor.copy(alpha = 0.35f),
                                    radius = 2.5.dp.toPx(),
                                    center = Offset(capMargin + step * i, h - capH / 2f - 4.dp.toPx())
                                )
                            }
                        }

                        NavigationStyle.TELEGRAM -> {
                            // Horizontal strip at top
                            drawRect(
                                color = surfaceColor,
                                topLeft = Offset(0f, 0f),
                                size = Size(w, barH)
                            )
                            val step = w / 6f
                            for (i in 1..5) {
                                drawCircle(
                                    color = if (i == 1) primaryColor else onSurfaceColor.copy(alpha = 0.35f),
                                    radius = 3.dp.toPx(),
                                    center = Offset(step * i, barH / 2f)
                                )
                            }
                        }

                        NavigationStyle.MATERIAL3 -> {
                            drawRect(
                                color = surfaceColor,
                                topLeft = Offset(0f, h - barH),
                                size = Size(w, barH)
                            )
                            // One highlighted pill indicator for selected item
                            val activeW = 28.dp.toPx()
                            val activeH = 8.dp.toPx()
                            drawRoundRect(
                                color = primaryColor.copy(alpha = 0.2f),
                                topLeft = Offset(w / 2f - activeW / 2f, h - barH + (barH - activeH) / 2f),
                                size = Size(activeW, activeH),
                                cornerRadius = CornerRadius(activeH / 2f)
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = 3.dp.toPx(),
                                center = Offset(w / 2f, h - barH / 2f)
                            )
                            for (i in listOf(w * 0.2f, w * 0.8f)) {
                                drawCircle(
                                    color = onSurfaceColor.copy(alpha = 0.3f),
                                    radius = 2.5.dp.toPx(),
                                    center = Offset(i, h - barH / 2f)
                                )
                            }
                        }

                        NavigationStyle.COMPACT -> {
                            val slimBarH = 10.dp.toPx()
                            drawRect(
                                color = surfaceColor,
                                topLeft = Offset(0f, h - slimBarH),
                                size = Size(w, slimBarH)
                            )
                            val step = w / 6f
                            for (i in 1..5) {
                                drawCircle(
                                    color = if (i == 2) primaryColor else onSurfaceColor.copy(alpha = 0.3f),
                                    radius = 2.dp.toPx(),
                                    center = Offset(step * i, h - slimBarH / 2f)
                                )
                            }
                        }

                        NavigationStyle.GLASS -> {
                            drawRect(
                                color = surfaceColor.copy(alpha = 0.55f),
                                topLeft = Offset(0f, h - barH),
                                size = Size(w, barH)
                            )
                            // Subtle top highlight line
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(0f, h - barH),
                                end = Offset(w, h - barH),
                                strokeWidth = 1.dp.toPx()
                            )
                            val step = w / 6f
                            for (i in 1..5) {
                                drawCircle(
                                    color = if (i == 3) primaryColor else onSurfaceColor.copy(alpha = 0.35f),
                                    radius = 3.dp.toPx(),
                                    center = Offset(step * i, h - barH / 2f)
                                )
                            }
                        }
                    }
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = primaryColor,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = styleName,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}
