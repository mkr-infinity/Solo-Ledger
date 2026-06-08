package com.solo.ledger.ui.components.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionItem(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    leftActionIcon: ImageVector = Icons.Filled.Delete,
    rightActionIcon: ImageVector = Icons.Filled.Edit,
    leftActionColor: Color = Color(0xFFFF4444),
    rightActionColor: Color = Color(0xFF2196F3),
    leftActionLabel: String = "Delete",
    rightActionLabel: String = "Edit",
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var resetKey by remember { mutableStateOf(0) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSwipeLeft()
                    true // allow full dismissal for delete
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSwipeRight()
                    false // do not dismiss; reset after edit
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    // Reset state after an edit swipe (StartToEnd) so the item springs back
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            SwipeBackground(
                dismissState = dismissState,
                leftActionColor = leftActionColor,
                rightActionColor = rightActionColor,
                leftActionIcon = leftActionIcon,
                rightActionIcon = rightActionIcon,
                leftActionLabel = leftActionLabel,
                rightActionLabel = rightActionLabel
            )
        },
        content = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    dismissState: SwipeToDismissBoxState,
    leftActionColor: Color,
    rightActionColor: Color,
    leftActionIcon: ImageVector,
    rightActionIcon: ImageVector,
    leftActionLabel: String,
    rightActionLabel: String
) {
    val direction = dismissState.dismissDirection

    val bgColor = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> leftActionColor
        SwipeToDismissBoxValue.StartToEnd -> rightActionColor
        else -> Color.Transparent
    }

    val progress = dismissState.progress.coerceIn(0f, 1f)
    val iconScale by animateFloatAsState(
        targetValue = if (progress > 0.3f) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 24.dp),
        contentAlignment = when (direction) {
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        when (direction) {
            SwipeToDismissBoxValue.EndToStart -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = leftActionIcon,
                        contentDescription = leftActionLabel,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(iconScale)
                    )
                    Text(
                        text = leftActionLabel,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = rightActionIcon,
                        contentDescription = rightActionLabel,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(iconScale)
                    )
                    Text(
                        text = rightActionLabel,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
            else -> Unit
        }
    }
}
