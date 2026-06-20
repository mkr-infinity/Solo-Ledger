package com.solo.ledger.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.SoloLedgerApplication
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoloLedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as SoloLedgerApplication).container

    val settings: StateFlow<UserSettings?> = container.settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    init {
        viewModelScope.launch {
            container.categoryRepository.ensureDefaultCategories(System.currentTimeMillis())
        }
    }

    fun completeOnboarding(template: BudgetTemplate?) {
        viewModelScope.launch {
            container.settingsRepository.completeOnboarding(template)
        }
    }
}
