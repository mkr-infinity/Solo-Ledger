package com.solo.ledger.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val userName: String = "",
    val currencySymbol: String = "₹",
    val currencyName: String = "Indian Rupee",
    val monthlyBudget: Double = 0.0
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsDataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    init {
        viewModelScope.launch {
            val current = settingsDataStore.settings.first()
            _state.update { it.copy(
                userName = current.userName,
                currencySymbol = current.currencySymbol,
                currencyName = current.currencyName,
                monthlyBudget = current.monthlyBudgetLimit
            ) }
        }
    }

    fun setUserName(name: String) {
        _state.update { it.copy(userName = name) }
        viewModelScope.launch {
            settingsDataStore.setUserName(name)
        }
    }

    fun setMonthlyBudget(amount: Double) {
        _state.update { it.copy(monthlyBudget = amount) }
        viewModelScope.launch {
            settingsDataStore.setMonthlyBudgetLimit(amount)
        }
    }

    fun setCurrency(symbol: String, name: String) {
        _state.update { it.copy(currencySymbol = symbol, currencyName = name) }
        viewModelScope.launch {
            settingsDataStore.setCurrencySymbol(symbol)
            settingsDataStore.setCurrencyName(name)
        }
    }

    fun setStoragePermissionDeferred(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setStoragePermissionDeferred(v)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val current = settingsDataStore.settings.first()
            if (current.firstInstallDate == 0L) {
                settingsDataStore.setFirstInstallDate(System.currentTimeMillis())
            }
            settingsDataStore.setOnboardingCompleted(true)
        }
    }
}
