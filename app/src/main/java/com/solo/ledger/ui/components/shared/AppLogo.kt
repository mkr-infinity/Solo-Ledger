package com.solo.ledger.ui.components.shared

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val px = size.toPx()
        drawLedgerIcon(px, color)
    }
}

private fun DrawScope.drawLedgerIcon(px: Float, color: Color) {
    val strokeWidth = px * 0.055f
    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

    val padH = px * 0.06f
    val padV = px * 0.08f
    val spineX = px * 0.5f
    val bookTop = padV
    val bookBottom = px - padV
    val bookLeft = padH
    val bookRight = px - padH

    // ── Outer book shape ──────────────────────────────────────────────────────
    // Left page
    val leftPage = Path().apply {
        moveTo(spineX, bookTop + px * 0.04f)
        cubicTo(
            spineX - px * 0.04f, bookTop,
            bookLeft + px * 0.06f, bookTop,
            bookLeft, bookTop + px * 0.06f
        )
        lineTo(bookLeft, bookBottom - px * 0.06f)
        cubicTo(
            bookLeft, bookBottom,
            bookLeft + px * 0.06f, bookBottom,
            spineX - px * 0.02f, bookBottom - px * 0.02f
        )
        lineTo(spineX, bookBottom - px * 0.02f)
        close()
    }
    drawPath(leftPage, color = color, style = stroke)

    // Right page
    val rightPage = Path().apply {
        moveTo(spineX, bookTop + px * 0.04f)
        cubicTo(
            spineX + px * 0.04f, bookTop,
            bookRight - px * 0.06f, bookTop,
            bookRight, bookTop + px * 0.06f
        )
        lineTo(bookRight, bookBottom - px * 0.06f)
        cubicTo(
            bookRight, bookBottom,
            bookRight - px * 0.06f, bookBottom,
            spineX + px * 0.02f, bookBottom - px * 0.02f
        )
        lineTo(spineX, bookBottom - px * 0.02f)
        close()
    }
    drawPath(rightPage, color = color, style = stroke)

    // Center spine
    drawLine(
        color = color,
        start = Offset(spineX, bookTop + px * 0.02f),
        end = Offset(spineX, bookBottom - px * 0.02f),
        strokeWidth = strokeWidth * 1.2f,
        cap = StrokeCap.Round
    )

    // ── Left page: 3 text entry lines ────────────────────────────────────────
    val lineStartX = bookLeft + px * 0.1f
    val lineEndX = spineX - px * 0.1f
    val lineAlpha = 0.75f
    val lineY1 = bookTop + (bookBottom - bookTop) * 0.30f
    val lineY2 = bookTop + (bookBottom - bookTop) * 0.48f
    val lineY3 = bookTop + (bookBottom - bookTop) * 0.66f

    for (y in listOf(lineY1, lineY2, lineY3)) {
        drawLine(
            color = color.copy(alpha = lineAlpha),
            start = Offset(lineStartX, y),
            end = Offset(lineEndX, y),
            strokeWidth = strokeWidth * 0.85f,
            cap = StrokeCap.Round
        )
    }

    // ── Right page: upward trend line chart ───────────────────────────────────
    val chartLeft = spineX + px * 0.09f
    val chartRight = bookRight - px * 0.09f
    val chartBottom = bookBottom - px * 0.2f
    val chartTop = bookTop + px * 0.22f

    val pt1 = Offset(chartLeft, chartBottom)
    val pt2 = Offset(chartLeft + (chartRight - chartLeft) * 0.45f, chartBottom - (chartBottom - chartTop) * 0.5f)
    val pt3 = Offset(chartRight, chartTop)

    // Trend line
    val trendPath = Path().apply {
        moveTo(pt1.x, pt1.y)
        lineTo(pt2.x, pt2.y)
        lineTo(pt3.x, pt3.y)
    }
    drawPath(trendPath, color = color.copy(alpha = 0.85f), style = stroke)

    // Dots at each data point
    val dotRadius = strokeWidth * 1.3f
    for (pt in listOf(pt1, pt2, pt3)) {
        drawCircle(color = color, radius = dotRadius, center = pt)
    }
}
