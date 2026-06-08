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
import androidx.compose.material3.Button
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
fun EmptyTransactions(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "emptyAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.88f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 260f),
        label = "emptyScale"
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
        Canvas(modifier = Modifier.size(160.dp)) {
            drawLedgerPlusIllustration(primaryColor)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Start tracking your spending by adding your first transaction",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(onClick = onAddClick) {
            Text(text = "Add Transaction")
        }
    }
}

private fun DrawScope.drawLedgerPlusIllustration(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val fillAlpha = 0.08f

    // Book outline
    val bookLeft = w * 0.08f
    val bookRight = w * 0.92f
    val bookTop = h * 0.08f
    val bookBottom = h * 0.78f
    val spineX = w * 0.50f
    val cr = 10.dp.toPx()

    // Left page fill
    val leftPath = Path().apply {
        moveTo(spineX, bookTop + 6.dp.toPx())
        cubicTo(spineX - 8.dp.toPx(), bookTop, bookLeft + cr, bookTop, bookLeft, bookTop + cr)
        lineTo(bookLeft, bookBottom - cr)
        cubicTo(bookLeft, bookBottom, bookLeft + cr, bookBottom, spineX - 4.dp.toPx(), bookBottom - 4.dp.toPx())
        lineTo(spineX, bookBottom - 4.dp.toPx())
        close()
    }
    drawPath(leftPath, color = color.copy(alpha = fillAlpha))
    drawPath(leftPath, color = color, style = stroke)

    // Right page fill
    val rightPath = Path().apply {
        moveTo(spineX, bookTop + 6.dp.toPx())
        cubicTo(spineX + 8.dp.toPx(), bookTop, bookRight - cr, bookTop, bookRight, bookTop + cr)
        lineTo(bookRight, bookBottom - cr)
        cubicTo(bookRight, bookBottom, bookRight - cr, bookBottom, spineX + 4.dp.toPx(), bookBottom - 4.dp.toPx())
        lineTo(spineX, bookBottom - 4.dp.toPx())
        close()
    }
    drawPath(rightPath, color = color.copy(alpha = fillAlpha))
    drawPath(rightPath, color = color, style = stroke)

    // Spine
    drawLine(
        color = color,
        start = Offset(spineX, bookTop + 4.dp.toPx()),
        end = Offset(spineX, bookBottom - 4.dp.toPx()),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Left page text lines
    val lineStartX = bookLeft + 10.dp.toPx()
    val lineEndX = spineX - 10.dp.toPx()
    listOf(0.32f, 0.47f, 0.62f).forEach { yFrac ->
        drawLine(
            color = color.copy(alpha = 0.45f),
            start = Offset(lineStartX, bookTop + (bookBottom - bookTop) * yFrac),
            end = Offset(lineEndX, bookTop + (bookBottom - bookTop) * yFrac),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Plus sign on right page – centered
    val plusCx = spineX + (bookRight - spineX) / 2f
    val plusCy = bookTop + (bookBottom - bookTop) / 2f
    val plusR = 18.dp.toPx()
    val plusStroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)

    // Circle background for plus
    drawCircle(color = color.copy(alpha = 0.12f), radius = plusR * 1.25f, center = Offset(plusCx, plusCy))
    drawCircle(color = color, radius = plusR * 1.25f, center = Offset(plusCx, plusCy), style = Stroke(width = 2.dp.toPx()))

    // Horizontal bar
    drawLine(color = color, start = Offset(plusCx - plusR * 0.65f, plusCy), end = Offset(plusCx + plusR * 0.65f, plusCy), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
    // Vertical bar
    drawLine(color = color, start = Offset(plusCx, plusCy - plusR * 0.65f), end = Offset(plusCx, plusCy + plusR * 0.65f), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)

    // Small shadow / base under book
    drawRoundRect(
        color = color.copy(alpha = 0.12f),
        topLeft = Offset(bookLeft + 8.dp.toPx(), bookBottom + 4.dp.toPx()),
        size = Size(bookRight - bookLeft - 16.dp.toPx(), 6.dp.toPx()),
        cornerRadius = CornerRadius(3.dp.toPx())
    )
}
