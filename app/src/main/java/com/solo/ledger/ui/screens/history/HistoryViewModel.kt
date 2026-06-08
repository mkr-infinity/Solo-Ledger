package com.solo.ledger.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.domain.usecase.DeleteTransactionUseCase
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import com.solo.ledger.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class FilterType { ALL, INCOME, EXPENSE }

data class HistoryUiState(
    val isLoading: Boolean = true,
    val groupedTransactions: LinkedHashMap<String, List<Pair<Transaction, Category?>>> = LinkedHashMap(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.ALL,
    val selectedCategoryId: String? = null,
    val dateRangeStart: LocalDate? = null,
    val dateRangeEnd: LocalDate? = null,
    val allCategories: List<Category> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsDataStore.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    // Mutable filter state
    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow(FilterType.ALL)
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _dateRangeStart = MutableStateFlow<LocalDate?>(null)
    private val _dateRangeEnd = MutableStateFlow<LocalDate?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        getTransactionsUseCase.getAll(),
        categoryRepository.getAll(),
        _searchQuery,
        _filterType,
        _selectedCategoryId
    ) { allTransactions, categories, query, filter, categoryId ->
        val categoryMap = categories.associateBy { it.id }
        val active = allTransactions.filter { !it.isDeleted }

        // Apply filters
        val filtered = active.filter { txn ->
            val matchesQuery = if (query.isBlank()) true else {
                val titleMatch = txn.title?.contains(query, ignoreCase = true) == true
                val notesMatch = txn.notes?.contains(query, ignoreCase = true) == true
                val catName = categoryMap[txn.categoryId]?.name ?: ""
                val catMatch = catName.contains(query, ignoreCase = true)
                val tagsMatch = txn.tags?.contains(query, ignoreCase = true) == true
                titleMatch || notesMatch || catMatch || tagsMatch
            }
            val matchesType = when (filter) {
                FilterType.ALL -> true
                FilterType.INCOME -> txn.type == TransactionType.INCOME
                FilterType.EXPENSE -> txn.type == TransactionType.EXPENSE
            }
            val matchesCategory = categoryId == null || txn.categoryId == categoryId

            matchesQuery && matchesType && matchesCategory
        }

        // Group by formatted date in reverse chronological order
        val grouped = LinkedHashMap<String, List<Pair<Transaction, Category?>>>()
        filtered
            .sortedByDescending { it.date.atTime(it.time) }
            .groupByTo(LinkedHashMap(), { DateUtils.formatDateFull(it.date) })
            .forEach { (dateKey, txns) ->
                grouped[dateKey] = txns.map { txn -> txn to categoryMap[txn.categoryId] }
            }

        val totalIncome = filtered
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = filtered
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        HistoryUiState(
            isLoading = false,
            groupedTransactions = grouped,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            searchQuery = query,
            filterType = filter,
            selectedCategoryId = categoryId,
            allCategories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    // ── Filter actions ────────────────────────────────────────────────────────

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: FilterType) {
        _filterType.value = filter
    }

    fun setCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filterType.value = FilterType.ALL
        _selectedCategoryId.value = null
        _dateRangeStart.value = null
        _dateRangeEnd.value = null
    }

    // ── Transaction actions ───────────────────────────────────────────────────

    fun softDelete(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase.softDelete(id)
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
}
