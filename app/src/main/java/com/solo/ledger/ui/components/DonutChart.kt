package com.solo.ledger.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

object DonutChart {
    data class Segment(
        val value: Float,
        val color: Color,
        val label: String
    )
}

@Composable
fun DonutChart(
    segments: List<DonutChart.Segment>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 20f
) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat()

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val radius = diameter / 2
        val topLeft = Offset(
            (size.width - diameter) / 2,
            (size.height - diameter) / 2
        )
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f

        segments.forEach { segment ->
            val sweepAngle = if (total > 0) (segment.value / total) * 360f else 0f
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}
