package com.kaif.ledger.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaif.ledger.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val logoAlpha = remember { mutableStateOf(0f) }
    val textAlpha = remember { mutableStateOf(0f) }
    val animatedLogoAlpha = animateFloatAsState(
        targetValue = logoAlpha.value,
        animationSpec = tween(800)
    ).value
    val animatedTextAlpha = animateFloatAsState(
        targetValue = textAlpha.value,
        animationSpec = tween(1000)
    ).value

    LaunchedEffect(Unit) {
        delay(300)
        logoAlpha.value = 1f
        delay(500)
        textAlpha.value = 1f
        delay(2500)
        navController.navigate(Screen.Onboarding1.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // Logo placeholder (SL icon)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    .alpha(animatedLogoAlpha),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SL",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(40.dp))

            // SOLO LEDGER text
            Text(
                "SOLO\nLEDGER",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animatedTextAlpha)
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(80.dp))

            // Subtitle
            Text(
                "POWERED BY ARCHITECTURAL PRECISION",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animatedTextAlpha)
            )
        }
    }
}
