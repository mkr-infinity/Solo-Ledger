package com.solo.ledger.data.repository

import com.solo.ledger.data.dao.CategorySpending
import com.solo.ledger.data.dao.ExpenseDao
import com.solo.ledger.data.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> =
        expenseDao.getExpensesByDateRange(startDate, endDate)

    fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(categoryId)

    fun searchExpenses(query: String): Flow<List<Expense>> =
        expenseDao.searchExpenses(query)

    fun getDeletedExpenses(): Flow<List<Expense>> = expenseDao.getDeletedExpenses()

    fun getTotalSpendingByDateRange(startDate: Long, endDate: Long): Flow<Double?> =
        expenseDao.getTotalSpendingByDateRange(startDate, endDate)

    fun getDailySpending(date: Long): Flow<Double?> =
        expenseDao.getDailySpending(date)

    fun getCategorySpending(startDate: Long, endDate: Long): Flow<List<CategorySpending>> =
        expenseDao.getCategorySpending(startDate, endDate)

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun softDeleteExpense(id: Long) = expenseDao.softDeleteExpense(id)

    suspend fun restoreExpense(id: Long) = expenseDao.restoreExpense(id)

    suspend fun permanentlyDeleteExpense(id: Long) = expenseDao.permanentlyDeleteExpense(id)

    suspend fun clearBin() = expenseDao.clearBin()

    suspend fun insertAll(expenses: List<Expense>) = expenseDao.insertAll(expenses)
}
