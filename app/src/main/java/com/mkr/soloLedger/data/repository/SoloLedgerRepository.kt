package com.mkr.soloLedger.data.repository

import androidx.lifecycle.LiveData
import com.mkr.soloLedger.data.dao.*
import com.mkr.soloLedger.data.entities.*

class SoloLedgerRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val userProfileDao: UserProfileDao,
    private val recurringExpenseDao: RecurringExpenseDao
) {
    // Expenses
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insert(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)
    suspend fun getAllExpensesOnce(): List<Expense> = expenseDao.getAllExpensesOnce()
    fun getExpensesByCategory(categoryId: Long): LiveData<List<Expense>> = expenseDao.getExpensesByCategory(categoryId)
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>> = expenseDao.getExpensesByDateRange(startDate, endDate)
    suspend fun getExpensesByDateRangeOnce(startDate: Long, endDate: Long): List<Expense> = expenseDao.getExpensesByDateRangeOnce(startDate, endDate)
    suspend fun getTotalAmountByDateRange(startDate: Long, endDate: Long): Double = expenseDao.getTotalAmountByDateRange(startDate, endDate) ?: 0.0
    fun getRecentExpenses(limit: Int): LiveData<List<Expense>> = expenseDao.getRecentExpenses(limit)
    fun searchExpenses(query: String): LiveData<List<Expense>> = expenseDao.searchExpenses(query)
    suspend fun getCategoryTotals(startDate: Long, endDate: Long): List<CategoryTotal> = expenseDao.getCategoryTotals(startDate, endDate)
    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()

    // Categories
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)
    suspend fun updateCategory(category: Category) = categoryDao.update(category)
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    suspend fun getAllCategoriesOnce(): List<Category> = categoryDao.getAllCategoriesOnce()
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)

    // Budget
    val budget: LiveData<Budget?> = budgetDao.getBudget()

    suspend fun insertBudget(budget: Budget) = budgetDao.insert(budget)
    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
    suspend fun getBudgetOnce(): Budget? = budgetDao.getBudgetOnce()

    // User Profile
    val userProfile: LiveData<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun insertUserProfile(profile: UserProfile) = userProfileDao.insert(profile)
    suspend fun updateUserProfile(profile: UserProfile) = userProfileDao.update(profile)
    suspend fun getUserProfileOnce(): UserProfile? = userProfileDao.getUserProfileOnce()

    // Recurring Expenses
    val activeRecurringExpenses: LiveData<List<RecurringExpense>> = recurringExpenseDao.getActiveRecurringExpenses()

    suspend fun insertRecurringExpense(expense: RecurringExpense) = recurringExpenseDao.insert(expense)
    suspend fun updateRecurringExpense(expense: RecurringExpense) = recurringExpenseDao.update(expense)
    suspend fun deleteRecurringExpense(expense: RecurringExpense) = recurringExpenseDao.delete(expense)
    suspend fun getDueRecurringExpenses(currentDate: Long): List<RecurringExpense> = recurringExpenseDao.getDueRecurringExpenses(currentDate)
    suspend fun getActiveRecurringExpensesOnce(): List<RecurringExpense> = recurringExpenseDao.getActiveRecurringExpensesOnce()
}
