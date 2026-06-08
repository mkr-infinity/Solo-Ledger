package com.solo.ledger.ui.components.empty

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyData(
    modifier: Modifier = Modifier,
    title: String = "No data available",
    subtitle: String = "Add transactions to see your analytics"
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "emptyDataAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.88f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 260f),
        label = "emptyDataScale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .graphicsLayer { this.alpha = alpha; scaleX = scale; scaleY = scale },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            drawEmptyBarChart(primaryColor)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

private fun DrawScope.drawEmptyBarChart(color: Color) {
    val w = size.width
    val h = size.height
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
    val dashedStroke = Stroke(width = 2.5.dp.toPx(), pathEffect = dashEffect, cap = StrokeCap.Round)

    val baselineY = h * 0.82f
    val barWidth = w * 0.16f
    val gap = w * 0.07f
    val totalBarsWidth = barWidth * 3 + gap * 2
    val startX = (w - totalBarsWidth) / 2f

    val barHeights = listOf(h * 0.55f, h * 0.38f, h * 0.68f)

    // Baseline
    drawLine(
        color = color.copy(alpha = 0.25f),
        start = Offset(startX - 8.dp.toPx(), baselineY),
        end = Offset(startX + totalBarsWidth + 8.dp.toPx(), baselineY),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    barHeights.forEachIndexed { index, barH ->
        val leftX = startX + index * (barWidth + gap)
        val topY = baselineY - barH
        // Dashed outline bar
        drawRoundRect(
            color = color.copy(alpha = 0.5f),
            topLeft = Offset(leftX, topY),
            size = Size(barWidth, barH),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = dashedStroke
        )
        // Very faint fill
        drawRoundRect(
            color = color.copy(alpha = 0.06f),
            topLeft = Offset(leftX, topY),
            size = Size(barWidth, barH),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
    }

    // Small X marks at tops of bars
    barHeights.forEachIndexed { index, barH ->
        val cx = startX + index * (barWidth + gap) + barWidth / 2f
        val cy = baselineY - barH - 12.dp.toPx()
        val xSize = 5.dp.toPx()
        val xStroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color.copy(alpha = 0.4f), start = Offset(cx - xSize, cy - xSize), end = Offset(cx + xSize, cy + xSize), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color.copy(alpha = 0.4f), start = Offset(cx + xSize, cy - xSize), end = Offset(cx - xSize, cy + xSize), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}
