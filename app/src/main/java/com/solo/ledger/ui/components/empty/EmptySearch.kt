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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptySearch(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "emptySearchAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.88f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 260f),
        label = "emptySearchScale"
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
            drawMagnifyingGlassX(primaryColor)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "No results found",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Try adjusting your search or filters",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        OutlinedButton(onClick = onClearFilters) {
            Text(text = "Clear filters")
        }
    }
}

private fun DrawScope.drawMagnifyingGlassX(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

    // Glass circle
    val circleRadius = w * 0.27f
    val circleCx = w * 0.38f
    val circleCy = h * 0.38f

    // Circle fill (faint)
    drawCircle(
        color = color.copy(alpha = 0.07f),
        radius = circleRadius,
        center = Offset(circleCx, circleCy)
    )
    drawCircle(
        color = color.copy(alpha = 0.65f),
        radius = circleRadius,
        center = Offset(circleCx, circleCy),
        style = stroke
    )

    // Handle
    val handleAngle = Math.toRadians(45.0).toFloat()
    val handleStartX = circleCx + circleRadius * 0.7f * kotlin.math.cos(handleAngle)
    val handleStartY = circleCy + circleRadius * 0.7f * kotlin.math.sin(handleAngle)
    val handleEndX = circleCx + circleRadius * 1.9f * kotlin.math.cos(handleAngle)
    val handleEndY = circleCy + circleRadius * 1.9f * kotlin.math.sin(handleAngle)
    drawLine(
        color = color.copy(alpha = 0.65f),
        start = Offset(handleStartX, handleStartY),
        end = Offset(handleEndX, handleEndY),
        strokeWidth = 4.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // X mark inside the glass
    val xSize = circleRadius * 0.38f
    val xStrokeW = 3.5.dp.toPx()
    drawLine(
        color = color,
        start = Offset(circleCx - xSize, circleCy - xSize),
        end = Offset(circleCx + xSize, circleCy + xSize),
        strokeWidth = xStrokeW,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = Offset(circleCx + xSize, circleCy - xSize),
        end = Offset(circleCx - xSize, circleCy + xSize),
        strokeWidth = xStrokeW,
        cap = StrokeCap.Round
    )

    // Small dots scattered for "searching" feel
    val dotPositions = listOf(
        Offset(w * 0.72f, h * 0.20f),
        Offset(w * 0.82f, h * 0.35f),
        Offset(w * 0.68f, h * 0.50f)
    )
    dotPositions.forEachIndexed { i, pos ->
        drawCircle(
            color = color.copy(alpha = 0.2f + i * 0.08f),
            radius = 4.dp.toPx() - i * 0.5.dp.toPx(),
            center = pos
        )
    }
}
