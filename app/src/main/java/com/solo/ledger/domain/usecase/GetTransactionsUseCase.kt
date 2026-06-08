package com.solo.ledger.domain.usecase

import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun getAll(): Flow<List<Transaction>> = repository.getAll()

    fun getDeleted(): Flow<List<Transaction>> = repository.getAllDeleted()

    fun getByMonth(year: Int, month: Int): Flow<List<Transaction>> {
        val monthYear = String.format("%04d-%02d", year, month)
        return repository.getByMonth(monthYear)
    }

    fun getRecent(limit: Int = 5): Flow<List<Transaction>> =
        repository.getRecentTransactions(limit)

    fun getByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> =
        repository.getByDateRange(start, end)

    suspend fun getTotalIncomeForMonth(year: Int, month: Int): Double {
        val monthYear = String.format("%04d-%02d", year, month)
        return repository.getSumByTypeAndMonth(TransactionType.INCOME, monthYear)
    }

    suspend fun getTotalExpenseForMonth(year: Int, month: Int): Double {
        val monthYear = String.format("%04d-%02d", year, month)
        return repository.getSumByTypeAndMonth(TransactionType.EXPENSE, monthYear)
    }
}
