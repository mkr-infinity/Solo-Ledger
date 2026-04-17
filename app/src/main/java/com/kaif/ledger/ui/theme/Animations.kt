package com.kaif.ledger.ui.theme

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.fadeIn(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseIn
        ),
        label = "fadeAlpha"
    )
    return this.graphicsLayer { this.alpha = alpha }
}

@Composable
fun Modifier.slideInFromLeft(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val translationX by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOut
        ),
        label = "slideX"
    )
    return this.graphicsLayer { this.translationX = translationX }
}

@Composable
fun Modifier.scaleIn(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOut
        ),
        label = "scale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
