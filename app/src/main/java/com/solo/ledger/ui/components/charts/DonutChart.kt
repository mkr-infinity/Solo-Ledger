package com.solo.ledger.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background

data class DonutChartEntry(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    data: List<DonutChartEntry>,
    modifier: Modifier = Modifier,
    centerText: String = "",
    centerSubtext: String = "",
    animated: Boolean = true
) {
    if (data.isEmpty()) return

    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total <= 0f) return

    // One Animatable per segment for staggered sweep animation
    val sweepAngles = remember(data) { data.map { Animatable(0f) } }

    LaunchedEffect(data, animated) {
        if (animated) {
            sweepAngles.forEachIndexed { index, animatable ->
                launch {
                    delay(index * 80L)
                    animatable.animateTo(
                        targetValue = (data[index].value / total) * 360f,
                        animationSpec = tween(durationMillis = 600)
                    )
                }
            }
        } else {
            sweepAngles.forEachIndexed { index, animatable ->
                animatable.snapTo((data[index].value / total) * 360f)
            }
        }
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val strokeWidth = 32.dp.toPx()
                val gap = 2.dp.toPx()                    // gap in degrees between segments
                val radius = (size.minDimension - strokeWidth) / 2f
                val topLeft = Offset(
                    x = center.x - radius,
                    y = center.y - radius
                )
                val arcSize = Size(radius * 2, radius * 2)

                var startAngle = -90f

                sweepAngles.forEachIndexed { index, animatable ->
                    val sweep = animatable.value
                    if (sweep <= 0f) return@forEachIndexed
                    val entry = data[index]
                    val gapDegrees = if (data.size > 1) gap else 0f
                    val actualSweep = (sweep - gapDegrees).coerceAtLeast(0f)

                    // Radial gradient for 3D convex look
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                entry.color.copy(alpha = 0.7f),
                                entry.color,
                                entry.color.copy(alpha = 0.85f)
                            ),
                            center = center,
                            radius = radius + strokeWidth / 2
                        ),
                        startAngle = startAngle + gapDegrees / 2f,
                        sweepAngle = actualSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep
                }

                // Inner shadow circle for depth
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius - strokeWidth * 0.3f
                    ),
                    radius = radius - strokeWidth * 0.4f,
                    center = center
                )

                // Center hole fill
                drawCircle(
                    color = surfaceColor,
                    radius = radius - strokeWidth / 2f - 2.dp.toPx(),
                    center = center
                )
            }

            // Center text overlay
            if (centerText.isNotEmpty() || centerSubtext.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (centerText.isNotEmpty()) {
                        Text(
                            text = centerText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = onSurfaceColor
                        )
                    }
                    if (centerSubtext.isNotEmpty()) {
                        Text(
                            text = centerSubtext,
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceColor.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Legend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { entry ->
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(entry.color)
                            )
                            Text(
                                text = entry.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Text(
                                text = if (total > 0f) "${"%.0f".format(entry.value / total * 100)}%" else "0%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = entry.color
                            )
                        }
                    }
                    // Fill second slot if row has only one item
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
