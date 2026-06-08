package com.solo.ledger.domain.usecase

import com.solo.ledger.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend fun softDelete(id: String): Result<Unit> = runCatching {
        repository.softDelete(id)
    }

    suspend fun restore(id: String): Result<Unit> = runCatching {
        repository.restore(id)
    }

    suspend fun permanentDelete(id: String): Result<Unit> = runCatching {
        repository.hardDelete(id)
    }

    /**
     * Immediately hard-deletes every record that is currently in the soft-deleted state.
     */
    suspend fun clearAll(): Result<Unit> = runCatching {
        val deletedTransactions = repository.getAllDeleted().first()
        deletedTransactions.forEach { tx ->
            repository.hardDelete(tx.id)
        }
    }

    /**
     * Purges soft-deleted transactions that are older than 15 days.
     */
    suspend fun purgeExpired(): Result<Unit> = runCatching {
        repository.purgeOldDeleted()
    }
}
