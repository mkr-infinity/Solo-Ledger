package com.solo.ledger.domain.usecase

import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend fun add(transaction: Transaction): Result<Unit> {
        if (transaction.amount <= 0.0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }
        return runCatching {
            repository.insert(transaction)
        }
    }

    suspend fun update(transaction: Transaction): Result<Unit> {
        if (transaction.amount <= 0.0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }
        return runCatching {
            val updated = transaction.copy(updatedAt = LocalDateTime.now())
            repository.update(updated)
        }
    }
}
