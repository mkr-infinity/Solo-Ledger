package com.solo.ledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.dao.CategorySpending
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Expense
import com.solo.ledger.data.model.SavingsGoal
import com.solo.ledger.data.preferences.UserPreferences
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.ExpenseRepository
import com.solo.ledger.data.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val savingsGoalRepository: SavingsGoalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Preferences
    val currentTheme: StateFlow<String> = userPreferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ledger_dark")

    val navigationStyle: StateFlow<String> = userPreferences.navigationStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "capsule")

    val onboardingComplete: StateFlow<Boolean> = userPreferences.onboardingComplete
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String> = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val monthlyBudget: StateFlow<Double> = userPreferences.monthlyBudget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000.0)

    val currencySymbol: StateFlow<String> = userPreferences.currencySymbol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "\u20B9")

    val currencyCode: StateFlow<String> = userPreferences.currencyCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "INR")

    val fontSize: StateFlow<String> = userPreferences.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "medium")

    val animationsEnabled: StateFlow<Boolean> = userPreferences.animationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val borderRadius: StateFlow<Float> = userPreferences.borderRadius
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16f)

    val dashboardOrder: StateFlow<String> = userPreferences.dashboardOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "budget,daily,savings,recent,category,graph")

    val avatarPath: StateFlow<String> = userPreferences.avatarPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // Data
    val allExpenses: StateFlow<List<Expense>> = expenseRepository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedExpenses: StateFlow<List<Expense>> = expenseRepository.getDeletedExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoal>> = savingsGoalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly spending
    val monthlySpending: StateFlow<Double> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfMonth = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val endOfMonth = cal.timeInMillis - 1

        expenseRepository.getTotalSpendingByDateRange(startOfMonth, endOfMonth)
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    // Daily spending
    val todaySpending: StateFlow<Double> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis

        expenseRepository.getDailySpending(todayStart)
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    // Category spending this month
    val categorySpending: StateFlow<List<CategorySpending>> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfMonth = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val endOfMonth = cal.timeInMillis - 1

        expenseRepository.getCategorySpending(startOfMonth, endOfMonth)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<Expense>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else expenseRepository.searchExpenses(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Expense operations
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense.copy(updatedAt = System.currentTimeMillis()))
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

    fun clearBin() {
        viewModelScope.launch {
            expenseRepository.clearBin()
        }
    }

    suspend fun getExpenseById(id: Long): Expense? = expenseRepository.getExpenseById(id)

    // Category operations
    fun addCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    // Savings Goal operations
    fun addSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            savingsGoalRepository.insertGoal(goal)
        }
    }

    fun updateSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            savingsGoalRepository.updateGoal(goal)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            savingsGoalRepository.deleteGoal(goal)
        }
    }

    // Preferences operations
    fun setTheme(themeKey: String) {
        viewModelScope.launch {
            userPreferences.setTheme(themeKey)
            val theme = com.solo.ledger.ui.theme.AppTheme.fromKey(themeKey)
            if (theme.isSquare) {
                userPreferences.setBorderRadius(0f)
            } else if (borderRadius.value == 0f) {
                userPreferences.setBorderRadius(16f)
            }
        }
    }

    fun setNavigationStyle(styleKey: String) {
        viewModelScope.launch { userPreferences.setNavigationStyle(styleKey) }
    }

    fun completeOnboarding(name: String, budget: Double) {
        viewModelScope.launch {
            userPreferences.setUserName(name)
            userPreferences.setMonthlyBudget(budget)
            userPreferences.setOnboardingComplete(true)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch { userPreferences.setUserName(name) }
    }

    fun updateMonthlyBudget(budget: Double) {
        viewModelScope.launch { userPreferences.setMonthlyBudget(budget) }
    }

    fun updateCurrency(code: String, symbol: String) {
        viewModelScope.launch {
            userPreferences.setCurrencyCode(code)
            userPreferences.setCurrencySymbol(symbol)
        }
    }

    fun updateFontSize(size: String) {
        viewModelScope.launch { userPreferences.setFontSize(size) }
    }

    fun updateAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setAnimationsEnabled(enabled) }
    }

    fun updateBorderRadius(radius: Float) {
        viewModelScope.launch { userPreferences.setBorderRadius(radius) }
    }

    fun updateDashboardOrder(order: String) {
        viewModelScope.launch { userPreferences.setDashboardOrder(order) }
    }

    fun updateAvatarPath(path: String) {
        viewModelScope.launch { userPreferences.setAvatarPath(path) }
    }

    // Helper for getting expenses for a specific date range
    fun getExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> =
        expenseRepository.getExpensesByDateRange(startDate, endDate)

    // Import/Export
    fun importExpenses(expenses: List<Expense>) {
        viewModelScope.launch {
            expenseRepository.insertAll(expenses)
        }
    }
}

class MainViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val savingsGoalRepository: SavingsGoalRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                expenseRepository,
                categoryRepository,
                savingsGoalRepository,
                userPreferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
