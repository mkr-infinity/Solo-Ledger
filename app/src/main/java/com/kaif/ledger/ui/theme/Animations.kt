package com.kaif.ledger.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Fade in animation for appearing elements
 */
fun Modifier.fadeIn(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    return this.then(
        Modifier.graphicsLayer {
            val infiniteTransition = InfiniteTransition(
                mutableListOf(),
                label = "FadeIn"
            )
            alpha = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = EaseIn
                ),
                label = "fadeAlpha"
            ).value
        }
    )
}

/**
 * Slide in animation from left
 */
fun Modifier.slideInFromLeft(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    return this.then(
        Modifier.graphicsLayer {
            val infiniteTransition = InfiniteTransition(
                mutableListOf(),
                label = "SlideIn"
            )
            translationX = infiniteTransition.animateFloat(
                initialValue = -100f,
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = EaseOut
                ),
                label = "slideX"
            ).value
        }
    )
}

/**
 * Scale animation for appearing elements
 */
fun Modifier.scaleIn(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    return this.then(
        Modifier.graphicsLayer {
            val infiniteTransition = InfiniteTransition(
                mutableListOf(),
                label = "ScaleIn"
            )
            val scale = infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = EaseOut
                ),
                label = "scale"
            ).value
            scaleX = scale
            scaleY = scale
        }
    )
}
