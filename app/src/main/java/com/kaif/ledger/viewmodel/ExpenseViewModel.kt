package com.kaif.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaif.ledger.data.database.entity.ExpenseEntity
import com.kaif.ledger.data.database.entity.CategoryEntity
import com.kaif.ledger.data.repository.ExpenseRepository
import com.kaif.ledger.data.repository.CategoryRepository
import com.kaif.ledger.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedDateMonth = MutableStateFlow(YearMonth.now())
    val selectedDateMonth: StateFlow<YearMonth> = _selectedDateMonth.asStateFlow()

    val allExpenses = expenseRepository.getAllActiveExpenses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val deletedExpenses = expenseRepository.getAllDeletedExpenses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allCategories = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val settings = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Calculate total outflow amount
    val totalExpenses = allExpenses
        .map { expenses ->
            expenses.sumOf { abs(it.amount) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Calculate monthly expenses
    val monthlyExpenses = combine(allExpenses, selectedDateMonth) { expenses, yearMonth ->
        val startDate = yearMonth.atDay(1).atStartOfDay()
        val endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59)
        expenses.filter { it.date >= startDate && it.date <= endDate }
            .sumOf { abs(it.amount) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Calculate daily expenses
    val dailyExpenses = combine(allExpenses, settings) { expenses, settings ->
        val today = LocalDateTime.now()
        val startOfDay = today.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = today.withHour(23).withMinute(59).withSecond(59)
        expenses.filter { it.date >= startOfDay && it.date <= endOfDay }
            .sumOf { abs(it.amount) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Calculate remaining budget
    val remainingBudget = combine(totalExpenses, settings) { total, appSettings ->
        val budget = appSettings?.monthlyBudget ?: 5000.0
        budget - total
    }.stateIn(viewModelScope, SharingStarted.Lazily, 5000.0)

    // Calculate daily burn percentage
    val dailyBurnPercentage = combine(dailyExpenses, settings) { daily, appSettings ->
        val dailyLimit = appSettings?.dailyBudgetLimit ?: 250.0
        if (dailyLimit > 0) {
            ((daily / dailyLimit) * 100).toFloat()
        } else {
            0f
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expense)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            expenseRepository.softDeleteExpense(id)
        }
    }

    fun restoreExpense(id: Long) {
        viewModelScope.launch {
            expenseRepository.restoreExpense(id)
        }
    }

    fun permanentlyDeleteExpense(id: Long) {
        viewModelScope.launch {
            expenseRepository.permanentlyDeleteExpense(id)
        }
    }

    fun clearDeletedExpenses() {
        viewModelScope.launch {
            expenseRepository.clearDeletedExpenses()
        }
    }

    fun selectDateMonth(yearMonth: YearMonth) {
        _selectedDateMonth.value = yearMonth
    }

    fun getExpensesByMonth(yearMonth: YearMonth): List<ExpenseEntity> {
        val startDate = yearMonth.atDay(1).atStartOfDay()
        val endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59)
        return allExpenses.value.filter { it.date >= startDate && it.date <= endDate }
    }

    fun getExpensesByDate(date: LocalDateTime): List<ExpenseEntity> {
        val startOfDay = date.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = date.withHour(23).withMinute(59).withSecond(59)
        return allExpenses.value.filter { it.date >= startOfDay && it.date <= endOfDay }
    }
}
