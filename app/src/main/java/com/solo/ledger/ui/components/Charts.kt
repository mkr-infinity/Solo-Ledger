package com.solo.ledger.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.LedgerTheme

data class Slice(val value: Float, val color: Color, val label: String)

@Composable
fun DonutChart(slices: List<Slice>, modifier: Modifier = Modifier) {
    val animate = com.solo.ledger.ui.theme.LocalAnimationsEnabled.current
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(slices, animate) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(if (animate) 700 else 0, easing = FastOutSlowInEasing))
    }
    val track = LedgerTheme.colors.outline
    Canvas(modifier) {
        val stroke = Stroke(width = size.minDimension * 0.16f, cap = StrokeCap.Butt)
        val inset = stroke.width / 2
        val arcSize = Size(size.minDimension - stroke.width, size.minDimension - stroke.width)
        val topLeft = Offset(inset, inset)
        drawArc(track, 0f, 360f, false, topLeft, arcSize, style = stroke)
        var start = -90f
        slices.forEach { s ->
            val sweep = 360f * (s.value / total) * progress.value
            drawArc(s.color, start, sweep, false, topLeft, arcSize, style = stroke)
            start += 360f * (s.value / total)
        }
    }
}

@Composable
fun LineChart(values: List<Float>, modifier: Modifier = Modifier) {
    val c = LedgerTheme.colors
    val animate = com.solo.ledger.ui.theme.LocalAnimationsEnabled.current
    val max = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
    val progress = remember(values) { Animatable(0f) }
    LaunchedEffect(values, animate) { progress.animateTo(1f, tween(if (animate) 700 else 0, easing = FastOutSlowInEasing)) }
    Canvas(modifier.fillMaxWidth().height(140.dp)) {
        if (values.size < 2) return@Canvas
        val stepX = size.width / (values.size - 1)
        val pts = values.mapIndexed { i, v -> Offset(i * stepX, size.height - (v / max) * size.height * progress.value) }
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(pts.first().x, pts.first().y)
            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
        }
        val fill = androidx.compose.ui.graphics.Path().apply {
            addPath(path); lineTo(pts.last().x, size.height); lineTo(pts.first().x, size.height); close()
        }
        drawPath(fill, brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(c.primary.copy(alpha = 0.25f), c.primary.copy(alpha = 0f))))
        drawPath(path, color = c.primary, style = Stroke(width = 6f, cap = StrokeCap.Round))
        pts.forEach { drawCircle(c.primary, radius = 6f, center = it) }
    }
}

@Composable
fun BarChart(values: List<Float>, modifier: Modifier = Modifier) {
    val c = LedgerTheme.colors
    val max = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
    val animate = com.solo.ledger.ui.theme.LocalAnimationsEnabled.current
    val progress = remember(values) { Animatable(0f) }
    LaunchedEffect(values, animate) { progress.animateTo(1f, tween(if (animate) 600 else 0, easing = FastOutSlowInEasing)) }
    Canvas(modifier.fillMaxWidth().height(140.dp)) {
        val n = values.size.coerceAtLeast(1)
        val gap = size.width * 0.03f
        val barW = (size.width - gap * (n + 1)) / n
        values.forEachIndexed { i, v ->
            val h = (v / max) * size.height * progress.value
            val x = gap + i * (barW + gap)
            drawRoundRect(
                color = c.primary,
                topLeft = Offset(x, size.height - h),
                size = Size(barW, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW * 0.3f, barW * 0.3f)
            )
        }
    }
}
