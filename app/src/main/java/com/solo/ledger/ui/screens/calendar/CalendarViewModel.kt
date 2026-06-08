package com.solo.ledger.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val transactionsByDate: Map<LocalDate, List<Pair<Transaction, Category?>>> = emptyMap(),
    val dailyExpense: Map<LocalDate, Double> = emptyMap(),
    val maxDailyExpense: Double = 0.0,
    val selectedDateTransactions: List<Pair<Transaction, Category?>> = emptyList()
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadMonthData()
    }

    private fun loadMonthData() {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val firstOfMonth = currentMonth.atDay(1)
            val lastOfMonth = currentMonth.atEndOfMonth()

            combine(
                getTransactionsUseCase.getByDateRange(firstOfMonth, lastOfMonth),
                categoryRepository.getAll()
            ) { transactions, categories ->
                Pair(transactions, categories)
            }.collect { (transactions, categories) ->
                val categoryMap = categories.associateBy { it.id }

                val transactionsByDate = transactions
                    .filter { !it.isDeleted }
                    .groupBy { it.date }
                    .mapValues { (_, txList) ->
                        txList.map { tx -> Pair(tx, categoryMap[tx.categoryId]) }
                    }

                val dailyExpense = transactions
                    .filter { !it.isDeleted && it.type == TransactionType.EXPENSE }
                    .groupBy { it.date }
                    .mapValues { (_, txList) -> txList.sumOf { it.amount } }

                val maxDailyExpense = dailyExpense.values.maxOrNull() ?: 0.0

                val selectedDate = _uiState.value.selectedDate
                val selectedDateTransactions = selectedDate?.let {
                    transactionsByDate[it] ?: emptyList()
                } ?: emptyList()

                _uiState.update { state ->
                    state.copy(
                        transactionsByDate = transactionsByDate,
                        dailyExpense = dailyExpense,
                        maxDailyExpense = maxDailyExpense,
                        selectedDateTransactions = selectedDateTransactions
                    )
                }
            }
        }
    }

    fun prevMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.minusMonths(1), selectedDate = null) }
        loadMonthData()
    }

    fun nextMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.plusMonths(1), selectedDate = null) }
        loadMonthData()
    }

    fun selectDate(date: LocalDate?) {
        _uiState.update { state ->
            val selectedDateTransactions = date?.let {
                state.transactionsByDate[it] ?: emptyList()
            } ?: emptyList()
            state.copy(
                selectedDate = date,
                selectedDateTransactions = selectedDateTransactions
            )
        }
    }
}
