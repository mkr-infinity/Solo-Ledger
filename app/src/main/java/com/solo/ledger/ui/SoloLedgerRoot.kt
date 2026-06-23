package com.solo.ledger.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solo.ledger.ui.navigation.SoloLedgerApp
import com.solo.ledger.ui.onboarding.OnboardingFlow
import com.solo.ledger.ui.theme.SoloLedgerTheme

@Composable
fun SoloLedgerRoot(onReady: () -> Unit) {
    val appVm: AppViewModel = viewModel()
    val settings by appVm.settings.collectAsState()

    LaunchedEffect(Unit) { onReady() }

    SoloLedgerTheme(
        themeId = settings.themeId,
        darkMode = settings.darkMode,
        cornerRadius = settings.cornerRadius,
        fontScale = settings.fontScale,
        highContrast = settings.highContrast,
        animationsEnabled = settings.animationsEnabled && !settings.reducedMotion
    ) {
        if (!settings.onboarded) {
            OnboardingFlow(onFinish = { name, budget -> appVm.completeOnboarding(name, budget) })
        } else {
            SoloLedgerApp(appVm = appVm)
        }
    }
}
