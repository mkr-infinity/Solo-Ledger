package com.kaif.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaif.ledger.data.database.entity.AppSettingsEntity
import com.kaif.ledger.data.repository.SettingsRepository
import com.kaif.ledger.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<AppSettingsEntity?> = settingsRepository.getSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    val currentTheme: StateFlow<ThemeType> = settings
        .map { it?.currentTheme?.let { theme -> 
            when (theme) {
                "MIDNIGHT" -> ThemeType.MIDNIGHT
                "TWILIGHT" -> ThemeType.TWILIGHT
                "AURORA" -> ThemeType.AURORA
                "NOON" -> ThemeType.NOON
                else -> ThemeType.MIDNIGHT
            }
        } ?: ThemeType.MIDNIGHT }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            ThemeType.MIDNIGHT
        )

    fun updateTheme(theme: ThemeType) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme.name)
        }
    }

    fun updateMonthlyBudget(budget: Double) {
        viewModelScope.launch {
            settingsRepository.updateMonthlyBudget(budget)
        }
    }

    fun updateShowCategoryField(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowCategoryField(show)
        }
    }

    fun updateShowNotesField(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowNotesField(show)
        }
    }

    fun updateShowTitleField(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowTitleField(show)
        }
    }

    fun updateShowDateField(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowDateField(show)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            settingsRepository.updateUserName(name)
        }
    }

    fun updateCurrency(code: String) {
        viewModelScope.launch {
            settingsRepository.updateCurrency(code)
        }
    }
}
