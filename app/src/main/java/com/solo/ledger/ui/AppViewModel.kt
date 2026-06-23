package com.solo.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.datastore.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private val settingsRepo = ServiceLocator.settingsRepository
    private val ledgerRepo = ServiceLocator.ledgerRepository

    val settings = settingsRepo.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings()
    )

    init { viewModelScope.launch { ledgerRepo.seedDefaultCategories() } }

    fun completeOnboarding(name: String, budget: Double) =
        viewModelScope.launch { settingsRepo.completeOnboarding(name, budget) }
    fun setTheme(id: String) = viewModelScope.launch { settingsRepo.setTheme(id) }
    fun setDark(v: Boolean) = viewModelScope.launch { settingsRepo.setDark(v) }
    fun setNavStyle(v: String) = viewModelScope.launch { settingsRepo.setNavStyle(v) }
    fun setCornerRadius(v: Int) = viewModelScope.launch { settingsRepo.setCornerRadius(v) }
    fun setFontScale(v: Float) = viewModelScope.launch { settingsRepo.setFontScale(v) }
    fun setReducedMotion(v: Boolean) = viewModelScope.launch { settingsRepo.setReducedMotion(v) }
    fun setHighContrast(v: Boolean) = viewModelScope.launch { settingsRepo.setHighContrast(v) }
    fun setName(v: String) = viewModelScope.launch { settingsRepo.setName(v) }
    fun setBudget(v: Double) = viewModelScope.launch { settingsRepo.setBudget(v) }
    fun setCurrency(v: String) = viewModelScope.launch { settingsRepo.setCurrency(v) }
    fun setAvatar(v: String) = viewModelScope.launch { settingsRepo.setAvatar(v) }
    fun setAnimations(v: Boolean) = viewModelScope.launch { settingsRepo.setAnimations(v) }
    fun setQuickAddNotes(v: Boolean) = viewModelScope.launch { settingsRepo.setQuickAddNotes(v) }
    fun setQuickAddTime(v: Boolean) = viewModelScope.launch { settingsRepo.setQuickAddTime(v) }
    fun toggleWidget(key: String, hidden: Boolean) = viewModelScope.launch { settingsRepo.toggleWidget(key, hidden) }
}
