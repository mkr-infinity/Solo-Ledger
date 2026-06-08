package com.solo.ledger.ui.screens.deleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.domain.usecase.DeleteTransactionUseCase
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class DeletedUiState(
    val isLoading: Boolean = true,
    val deletedItems: List<Pair<Transaction, Category?>> = emptyList(),
    val daysUntilPurge: Map<String, Long> = emptyMap()
)

@HiltViewModel
class DeletedViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val categoryRepository: CategoryRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeletedUiState())
    val uiState: StateFlow<DeletedUiState> = _uiState.asStateFlow()

    val settings = settingsDataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = com.solo.ledger.data.datastore.AppSettings()
    )

    init {
        viewModelScope.launch {
            deleteTransactionUseCase.purgeExpired()
        }

        viewModelScope.launch {
            combine(
                getTransactionsUseCase.getDeleted(),
                categoryRepository.getAll()
            ) { deletedTransactions, categories ->
                val categoryMap = categories.associateBy { it.id }
                val paired = deletedTransactions.map { tx ->
                    tx to categoryMap[tx.categoryId]
                }
                val purgeMap = deletedTransactions.associate { tx ->
                    val deletedAtDate = tx.deletedAt?.toLocalDate() ?: LocalDate.now()
                    val daysSinceDeleted = ChronoUnit.DAYS.between(deletedAtDate, LocalDate.now())
                    val daysLeft = (15L - daysSinceDeleted).coerceAtLeast(0L)
                    tx.id to daysLeft
                }
                Pair(paired, purgeMap)
            }.collect { (paired, purgeMap) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        deletedItems = paired,
                        daysUntilPurge = purgeMap
                    )
                }
            }
        }
    }

    fun restore(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase.restore(id)
        }
    }

    fun permanentDelete(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase.permanentDelete(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            deleteTransactionUseCase.clearAll()
        }
    }
}
