package com.solo.ledger.ui.components.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.CardStyle
import com.solo.ledger.ui.theme.LocalThemeDefinition

@Composable
fun ThemedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val themeDefinition = LocalThemeDefinition.current
    val cornerRadius = themeDefinition.cornerRadiusCard
    val shape = RoundedCornerShape(cornerRadius)
    val isDark = isSystemInDarkTheme()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    val scaledModifier = modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    val clickableModifier: Modifier = if (onClick != null) {
        scaledModifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = onClick
        )
    } else scaledModifier

    when (themeDefinition.cardStyle) {
        CardStyle.FLAT -> {
            Card(
                modifier = clickableModifier,
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(content = content)
            }
        }

        CardStyle.ELEVATED -> {
            val shadowColor = if (isDark) Color(0x80000000) else Color(0x1A000000)

            val shadowedModifier = (if (onClick != null) {
                scaledModifier.clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true),
                    onClick = onClick
                )
            } else scaledModifier).drawBehind {
                for (i in 1..3) {
                    val offsetY = (i * 2).dp.toPx()
                    val offsetX = (i * 0.5f).dp.toPx()
                    val blurExtra = (i * 4).dp.toPx() * 0.5f
                    val alpha = (0.18f - i * 0.04f).coerceAtLeast(0f)
                    drawRoundRect(
                        color = shadowColor.copy(alpha = alpha),
                        topLeft = Offset(offsetX, offsetY),
                        size = Size(
                            (size.width - offsetX).coerceAtLeast(0f),
                            (size.height - offsetY + blurExtra).coerceAtLeast(0f)
                        ),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                }
            }

            Card(
                modifier = shadowedModifier,
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(content = content)
            }
        }

        CardStyle.OUTLINED -> {
            Card(
                modifier = clickableModifier,
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(content = content)
            }
        }

        CardStyle.GLASS -> {
            val glassColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            val highlightColor = Color.White.copy(alpha = 0.15f)

            Card(
                modifier = clickableModifier,
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, highlightColor),
                colors = CardDefaults.cardColors(containerColor = glassColor)
            ) {
                Column(content = content)
            }
        }
    }
}
