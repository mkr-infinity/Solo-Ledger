package com.solo.ledger.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS, INFO, WARNING, ERROR, FUN
}

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO,
    val icon: ImageVector? = null
)

@Composable
fun AppToast(
    toastData: ToastData?,
    onDismiss: () -> Unit
) {
    LaunchedEffect(toastData) {
        if (toastData != null) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = toastData != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        toastData?.let { data ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val containerColor = when (data.type) {
                    ToastType.SUCCESS -> MaterialTheme.colorScheme.secondaryContainer
                    ToastType.INFO -> MaterialTheme.colorScheme.primaryContainer
                    ToastType.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
                    ToastType.ERROR -> MaterialTheme.colorScheme.errorContainer
                    ToastType.FUN -> MaterialTheme.colorScheme.surfaceVariant
                }
                val contentColor = when (data.type) {
                    ToastType.SUCCESS -> MaterialTheme.colorScheme.onSecondaryContainer
                    ToastType.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
                    ToastType.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
                    ToastType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                    ToastType.FUN -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val icon = data.icon ?: when (data.type) {
                    ToastType.SUCCESS -> Icons.Filled.CheckCircle
                    ToastType.INFO -> Icons.Filled.Info
                    ToastType.WARNING -> Icons.Filled.Warning
                    ToastType.ERROR -> Icons.Filled.Error
                    ToastType.FUN -> Icons.Filled.SentimentSatisfied
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(containerColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = contentColor
                    )
                }
            }
        }
    }
}
