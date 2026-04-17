package com.kaif.ledger.data.repository

import com.kaif.ledger.data.database.dao.ExpenseDao
import com.kaif.ledger.data.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllActiveExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllActiveExpenses()
    }

    fun getAllDeletedExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllDeletedExpenses()
    }

    fun getExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    fun getExpensesByCategory(categoryId: Long): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(categoryId)
    }

    suspend fun getExpenseById(id: Long): ExpenseEntity? {
        return expenseDao.getExpenseById(id)
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun softDeleteExpense(id: Long) {
        expenseDao.softDeleteExpense(id)
    }

    suspend fun permanentlyDeleteExpense(id: Long) {
        expenseDao.permanentlyDeleteExpense(id)
    }

    suspend fun restoreExpense(id: Long) {
        expenseDao.restoreExpense(id)
    }

    suspend fun clearDeletedExpenses() {
        expenseDao.clearDeletedExpenses()
    }

    fun getTotalExpenses(): Flow<Double?> {
        return expenseDao.getTotalExpenses()
    }

    fun getTotalExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double?> {
        return expenseDao.getTotalExpensesByDateRange(startDate, endDate)
    }
}
