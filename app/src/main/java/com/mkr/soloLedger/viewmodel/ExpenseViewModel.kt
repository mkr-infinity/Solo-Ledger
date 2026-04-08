package com.mkr.soloLedger.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mkr.soloLedger.data.database.SoloLedgerDatabase
import com.mkr.soloLedger.data.entities.Expense
import com.mkr.soloLedger.data.repository.SoloLedgerRepository
import com.mkr.soloLedger.utils.AutoCategorizer
import com.mkr.soloLedger.utils.getEndOfMonth
import com.mkr.soloLedger.utils.getStartOfMonth
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SoloLedgerRepository
    val allExpenses: LiveData<List<Expense>>

    init {
        val db = SoloLedgerDatabase.getDatabase(application)
        repository = SoloLedgerRepository(
            db.expenseDao(), db.categoryDao(), db.budgetDao(),
            db.userProfileDao(), db.recurringExpenseDao()
        )
        allExpenses = repository.allExpenses
    }

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    fun getRecentExpenses(limit: Int = 5) = repository.getRecentExpenses(limit)

    fun getExpensesByCategory(categoryId: Long) = repository.getExpensesByCategory(categoryId)

    fun getExpensesByDateRange(startDate: Long, endDate: Long) =
        repository.getExpensesByDateRange(startDate, endDate)

    fun searchExpenses(query: String) = repository.searchExpenses(query)

    fun suggestCategory(title: String): String = AutoCategorizer.categorize(title)

    fun getCurrentMonthExpenses(): LiveData<List<Expense>> {
        val start = getStartOfMonth()
        val end = getEndOfMonth()
        return repository.getExpensesByDateRange(start, end)
    }
}
