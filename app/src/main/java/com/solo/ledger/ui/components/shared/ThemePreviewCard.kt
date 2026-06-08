package com.solo.ledger.ui.components.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo.ledger.ui.theme.ThemeDefinition

@Composable
fun ThemePreviewCard(
    theme: ThemeDefinition,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.95f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "themeCardScale"
    )

    val borderColor = if (isSelected)
        theme.colorScheme.primary
    else
        theme.colorScheme.outline.copy(alpha = 0.5f)

    val borderWidth = if (isSelected) 2.5.dp else 1.dp
    val cardCorner = minOf(theme.cornerRadiusCard, 12.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box {
            Card(
                modifier = Modifier
                    .size(width = 100.dp, height = 140.dp)
                    .clickable(
                        indication = ripple(bounded = true),
                        interactionSource = null,
                        onClick = onClick
                    ),
                shape = RoundedCornerShape(cardCorner),
                border = BorderStroke(borderWidth, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
                colors = CardDefaults.cardColors(containerColor = theme.colorScheme.background)
            ) {
                Canvas(modifier = Modifier.size(width = 100.dp, height = 140.dp)) {
                    val w = size.width
                    val h = size.height
                    val cr = cardCorner.toPx()

                    // Top strip – nav/surface container area (top 20%)
                    val navBarHeight = h * 0.20f
                    drawRoundRect(
                        color = theme.colorScheme.surfaceContainer,
                        topLeft = Offset.Zero,
                        size = Size(w, navBarHeight + cr),
                        cornerRadius = CornerRadius(cr)
                    )
                    drawRect(
                        color = theme.colorScheme.surfaceContainer,
                        topLeft = Offset(0f, navBarHeight),
                        size = Size(w, cr)
                    )

                    // Middle card area (60%)
                    val cardTop = navBarHeight + 8.dp.toPx()
                    val cardHeight = h * 0.55f
                    val cardLeft = 8.dp.toPx()
                    val cardRight = w - 8.dp.toPx()
                    val cardCr = minOf(theme.cornerRadiusCard * 0.5f, 8.dp).toPx()
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                theme.colorScheme.primaryContainer,
                                theme.colorScheme.secondaryContainer
                            ),
                            start = Offset(cardLeft, cardTop),
                            end = Offset(cardRight, cardTop + cardHeight)
                        ),
                        topLeft = Offset(cardLeft, cardTop),
                        size = Size(cardRight - cardLeft, cardHeight),
                        cornerRadius = CornerRadius(cardCr)
                    )

                    // Simulate a balance text line
                    val lineY = cardTop + cardHeight * 0.35f
                    val lineColor = theme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    drawRoundRect(
                        color = lineColor,
                        topLeft = Offset(cardLeft + 8.dp.toPx(), lineY),
                        size = Size(w * 0.4f, 5.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                    drawRoundRect(
                        color = lineColor.copy(alpha = 0.5f),
                        topLeft = Offset(cardLeft + 8.dp.toPx(), lineY + 10.dp.toPx()),
                        size = Size(w * 0.25f, 3.dp.toPx()),
                        cornerRadius = CornerRadius(1.5.dp.toPx())
                    )

                    // Bottom strip – background color (bottom 20%)
                    val bottomStripTop = h * 0.80f
                    drawRect(
                        color = theme.colorScheme.surfaceVariant,
                        topLeft = Offset(0f, bottomStripTop),
                        size = Size(w, h - bottomStripTop)
                    )
                    // Tab indicator dots
                    val dotY = h - 10.dp.toPx()
                    val dotRadius = 2.5.dp.toPx()
                    val dotSpacing = w / 5f
                    for (i in 1..4) {
                        val dotColor = if (i == 2) theme.colorScheme.primary
                        else theme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        drawCircle(
                            color = dotColor,
                            radius = if (i == 2) dotRadius * 1.2f else dotRadius,
                            center = Offset(dotSpacing * i, dotY)
                        )
                    }
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = theme.colorScheme.primary,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = theme.name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}
