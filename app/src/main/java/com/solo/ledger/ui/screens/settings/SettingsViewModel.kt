package com.solo.ledger.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.domain.usecase.DeleteTransactionUseCase
import com.solo.ledger.domain.usecase.ExportJsonUseCase
import com.solo.ledger.domain.usecase.ExportPdfUseCase
import com.solo.ledger.domain.usecase.ImportJsonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val operationResult: OperationResult? = null
)

sealed class OperationResult {
    data class Success(val message: String) : OperationResult()
    data class Error(val message: String) : OperationResult()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val importJsonUseCase: ImportJsonUseCase,
    private val exportJsonUseCase: ExportJsonUseCase,
    private val exportPdfUseCase: ExportPdfUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _operationState = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.settings,
        _operationState
    ) { settings, opState ->
        opState.copy(settings = settings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setTheme(id: String) {
        viewModelScope.launch {
            settingsDataStore.setActiveThemeId(id)
        }
    }

    fun setNavigationStyle(style: String) {
        viewModelScope.launch {
            settingsDataStore.setNavigationStyle(style)
        }
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch {
            settingsDataStore.setCurrencySymbol(symbol)
        }
    }

    fun setCurrencyName(name: String) {
        viewModelScope.launch {
            settingsDataStore.setCurrencyName(name)
        }
    }

    fun setShowTitleField(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowTitleField(v)
        }
    }

    fun setShowCategoryField(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowCategoryField(v)
        }
    }

    fun setShowNotesField(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowNotesField(v)
        }
    }

    fun setShowDateField(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowDateField(v)
        }
    }

    fun setShowTimeField(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowTimeField(v)
        }
    }

    fun setMonthlyBudgetLimit(v: Double) {
        viewModelScope.launch {
            settingsDataStore.setMonthlyBudgetLimit(v)
        }
    }

    fun setSavingsTarget(v: Double) {
        viewModelScope.launch {
            settingsDataStore.setSavingsTarget(v)
        }
    }

    fun setTrackBudgetCycles(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setTrackBudgetCycles(v)
        }
    }

    fun setFontScale(v: Float) {
        viewModelScope.launch {
            settingsDataStore.setFontScale(v)
        }
    }

    fun setReducedMotion(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setReducedMotion(v)
        }
    }

    fun setHighContrast(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setHighContrast(v)
        }
    }

    fun setLargerTouchTargets(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setLargerTouchTargets(v)
        }
    }

    fun setDashboardSectionOrder(order: String) {
        viewModelScope.launch {
            settingsDataStore.setDashboardSectionOrder(order)
        }
    }

    fun setPreferredChartType(type: String) {
        viewModelScope.launch {
            settingsDataStore.setPreferredChartType(type)
        }
    }

    fun setAnimationIntensity(intensity: String) {
        viewModelScope.launch {
            settingsDataStore.setAnimationIntensity(intensity)
        }
    }

    fun setIconStyle(style: String) {
        viewModelScope.launch {
            settingsDataStore.setIconStyle(style)
        }
    }

    fun setCardStyle(style: String) {
        viewModelScope.launch {
            settingsDataStore.setCardStyle(style)
        }
    }

    fun setBorderRadiusStyle(style: String) {
        viewModelScope.launch {
            settingsDataStore.setBorderRadiusStyle(style)
        }
    }

    fun setSwipeLeftAction(action: String) {
        viewModelScope.launch {
            settingsDataStore.setSwipeLeftAction(action)
        }
    }

    fun setSwipeRightAction(action: String) {
        viewModelScope.launch {
            settingsDataStore.setSwipeRightAction(action)
        }
    }

    fun setAutoCheckUpdates(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAutoCheckUpdates(v)
        }
    }

    fun setLogsEnabled(v: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setLogsEnabled(v)
        }
    }

    fun exportJson(uri: Uri) {
        viewModelScope.launch {
            _operationState.update { it.copy(isExporting = true, operationResult = null) }
            try {
                exportJsonUseCase.export(uri)
                _operationState.update {
                    it.copy(
                        isExporting = false,
                        operationResult = OperationResult.Success("Data exported successfully")
                    )
                }
            } catch (e: Exception) {
                _operationState.update {
                    it.copy(
                        isExporting = false,
                        operationResult = OperationResult.Error(
                            "Export failed: ${e.localizedMessage ?: "Unknown error"}"
                        )
                    )
                }
            }
        }
    }

    fun importJson(uri: Uri) {
        viewModelScope.launch {
            _operationState.update { it.copy(isImporting = true, operationResult = null) }
            try {
                importJsonUseCase.import(uri)
                deleteTransactionUseCase.purgeExpired()
                _operationState.update {
                    it.copy(
                        isImporting = false,
                        operationResult = OperationResult.Success("Data imported successfully")
                    )
                }
            } catch (e: Exception) {
                _operationState.update {
                    it.copy(
                        isImporting = false,
                        operationResult = OperationResult.Error(
                            "Import failed: ${e.localizedMessage ?: "Unknown error"}"
                        )
                    )
                }
            }
        }
    }

    fun exportPdf(uri: Uri) {
        viewModelScope.launch {
            _operationState.update { it.copy(isExporting = true, operationResult = null) }
            try {
                exportPdfUseCase.export(uri)
                _operationState.update {
                    it.copy(
                        isExporting = false,
                        operationResult = OperationResult.Success("PDF exported successfully")
                    )
                }
            } catch (e: Exception) {
                _operationState.update {
                    it.copy(
                        isExporting = false,
                        operationResult = OperationResult.Error(
                            "PDF export failed: ${e.localizedMessage ?: "Unknown error"}"
                        )
                    )
                }
            }
        }
    }

    fun clearOperationResult() {
        _operationState.update { it.copy(operationResult = null) }
    }
}
