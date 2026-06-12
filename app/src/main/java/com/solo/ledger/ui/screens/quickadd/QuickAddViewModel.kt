package com.solo.ledger.ui.screens.quickadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.domain.usecase.AddTransactionUseCase
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import com.solo.ledger.util.DateUtils

data class QuickAddUiState(
    val amountString: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryId: String? = null,
    val title: String = "",
    val notes: String = "",
    val tags: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val categories: List<Category> = emptyList(),
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val editingTransactionId: String? = null,
    val currencySymbol: String = "₹"
)

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAddUiState())
    val uiState: StateFlow<QuickAddUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                categoryRepository.getAll(),
                settingsDataStore.settings
            ) { categories, settings ->
                Pair(categories, settings)
            }.collect { (categories, settings) ->
                _uiState.update { state ->
                    val filteredCategories = categories.filter { category ->
                        category.type == com.solo.ledger.data.model.CategoryType.BOTH ||
                                (state.type == TransactionType.INCOME && category.type == com.solo.ledger.data.model.CategoryType.INCOME) ||
                                (state.type == TransactionType.EXPENSE && category.type == com.solo.ledger.data.model.CategoryType.EXPENSE)
                    }
                    val defaultCategoryId = if (state.selectedCategoryId == null && !state.isEditMode) {
                        settings.defaultCategoryId
                    } else {
                        state.selectedCategoryId
                    }
                    state.copy(
                        categories = filteredCategories,
                        selectedCategoryId = defaultCategoryId,
                        currencySymbol = settings.currencySymbol
                    )
                }
            }
        }
    }

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            val transaction = getTransactionsUseCase.getAll().first().find { it.id == id }
                ?: return@launch
            _uiState.update { state ->
                state.copy(
                    isEditMode = true,
                    editingTransactionId = transaction.id,
                    amountString = transaction.amount.toString(),
                    type = transaction.type,
                    selectedCategoryId = transaction.categoryId,
                    title = transaction.title ?: "",
                    notes = transaction.notes ?: "",
                    tags = transaction.tags ?: "",
                    date = transaction.date,
                    time = transaction.time,
                    error = null,
                    isSuccess = false
                )
            }
        }
    }

    fun updateAmount(s: String) {
        if (s.isEmpty() || s.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update { it.copy(amountString = s, error = null) }
        }
    }

    fun updateType(type: TransactionType) {
        _uiState.update { state ->
            val filteredCategories = state.categories.filter { category ->
                category.type == com.solo.ledger.data.model.CategoryType.BOTH ||
                        (type == TransactionType.INCOME && category.type == com.solo.ledger.data.model.CategoryType.INCOME) ||
                        (type == TransactionType.EXPENSE && category.type == com.solo.ledger.data.model.CategoryType.EXPENSE)
            }
            state.copy(
                type = type,
                selectedCategoryId = null,
                categories = filteredCategories
            )
        }
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _uiState.update { state ->
                    val filtered = categories.filter { category ->
                        category.type == com.solo.ledger.data.model.CategoryType.BOTH ||
                                (state.type == TransactionType.INCOME && category.type == com.solo.ledger.data.model.CategoryType.INCOME) ||
                                (state.type == TransactionType.EXPENSE && category.type == com.solo.ledger.data.model.CategoryType.EXPENSE)
                    }
                    state.copy(categories = filtered)
                }
            }
        }
    }

    fun updateCategory(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun updateTitle(s: String) {
        _uiState.update { it.copy(title = s) }
    }

    fun updateNotes(s: String) {
        _uiState.update { it.copy(notes = s) }
    }

    fun updateTags(s: String) {
        _uiState.update { it.copy(tags = s) }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { it.copy(time = time) }
    }

    fun submit() {
        val state = _uiState.value
        val amount = state.amountString.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                val now = java.time.LocalDateTime.now()
                if (state.isEditMode && state.editingTransactionId != null) {
                    // Find the original to preserve createdAt / deletedAt
                    val original = getTransactionsUseCase.getAll()
                        .first()
                        .find { it.id == state.editingTransactionId }
                    val updated = (original ?: Transaction(
                        id = state.editingTransactionId,
                        amount = amount,
                        type = state.type,
                        categoryId = state.selectedCategoryId ?: "",
                        title = null,
                        notes = null,
                        tags = null,
                        date = state.date,
                        time = state.time,
                        dayOfWeek = DateUtils.getDayOfWeek(state.date),
                        createdAt = now,
                        updatedAt = now
                    )).copy(
                        amount = amount,
                        type = state.type,
                        categoryId = state.selectedCategoryId ?: "",
                        title = state.title.takeIf { it.isNotBlank() },
                        notes = state.notes.takeIf { it.isNotBlank() },
                        tags = state.tags.takeIf { it.isNotBlank() }
                            ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                            ?.joinToString(","),
                        date = state.date,
                        time = state.time,
                        dayOfWeek = DateUtils.getDayOfWeek(state.date),
                        updatedAt = now
                    )
                    addTransactionUseCase.update(updated)
                } else {
                    val transaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        amount = amount,
                        type = state.type,
                        categoryId = state.selectedCategoryId ?: "",
                        title = state.title.takeIf { it.isNotBlank() },
                        notes = state.notes.takeIf { it.isNotBlank() },
                        tags = state.tags.takeIf { it.isNotBlank() }
                            ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                            ?.joinToString(","),
                        date = state.date,
                        time = state.time,
                        dayOfWeek = DateUtils.getDayOfWeek(state.date),
                        createdAt = now,
                        updatedAt = now,
                        isDeleted = false,
                        deletedAt = null
                    )
                    addTransactionUseCase.add(transaction)
                }
                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                delay(500)
                if (!state.isEditMode) resetState()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        error = e.message ?: "Failed to save transaction"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update {
            QuickAddUiState(
                date = LocalDate.now(),
                time = LocalTime.now()
            )
        }
    }
}
