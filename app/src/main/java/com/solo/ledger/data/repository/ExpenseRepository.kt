package com.solo.ledger.data.repository

import com.solo.ledger.data.local.dao.ExpenseDao
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
) {
    fun observeActiveExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeActiveExpenses()

    fun observeDeletedExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeDeletedExpenses()

    fun observeExpense(id: String): Flow<ExpenseEntity?> = expenseDao.observeExpense(id)

    fun searchActiveExpenses(query: String): Flow<List<ExpenseEntity>> = expenseDao.searchActiveExpenses(query.trim())

    fun observeExpensesBetween(startEpochDay: Long, endEpochDay: Long): Flow<List<ExpenseEntity>> =
        expenseDao.observeExpensesBetween(startEpochDay, endEpochDay)

    suspend fun upsert(expense: ExpenseEntity) = expenseDao.upsert(expense)

    suspend fun moveToBin(id: String, deletedAtMillis: Long) = expenseDao.moveToBin(id, deletedAtMillis)

    suspend fun restore(id: String, restoredAtMillis: Long) = expenseDao.restore(id, restoredAtMillis)

    suspend fun deletePermanently(expense: ExpenseEntity) = expenseDao.deletePermanently(expense)

    suspend fun clearBin() = expenseDao.clearBin()
}
