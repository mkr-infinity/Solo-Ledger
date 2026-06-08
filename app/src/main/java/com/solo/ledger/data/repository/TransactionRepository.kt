package com.solo.ledger.data.repository

import com.solo.ledger.data.db.dao.TransactionDao
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {

    // ── Read ──────────────────────────────────────────────────────────────────

    fun getAll(): Flow<List<Transaction>> = dao.getAll()

    fun getAllDeleted(): Flow<List<Transaction>> = dao.getAllDeleted()

    fun getByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> =
        dao.getByDateRange(start, end)

    fun getByCategory(categoryId: String): Flow<List<Transaction>> =
        dao.getByCategory(categoryId)

    /**
     * Returns all non-deleted transactions for the given month.
     * @param monthYear "YYYY-MM" — will be used as a LIKE prefix in SQL.
     */
    fun getByMonth(monthYear: String): Flow<List<Transaction>> =
        dao.getByMonth(monthYear)

    /**
     * Returns the sum of all amounts for the given [type] in the given [monthYear].
     * @param monthYear "YYYY-MM"
     */
    suspend fun getSumByTypeAndMonth(type: TransactionType, monthYear: String): Double =
        dao.getSumByTypeAndMonth(type.name, monthYear)

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> =
        dao.getRecentTransactions(limit)

    suspend fun getById(id: String): Transaction? = dao.getById(id)

    // ── Write ─────────────────────────────────────────────────────────────────

    suspend fun insert(transaction: Transaction) = dao.insert(transaction)

    suspend fun update(transaction: Transaction) = dao.update(transaction)

    /**
     * Soft-deletes a transaction: sets [Transaction.isDeleted] = true and records [deletedAt].
     * The record is retained in the DB and will appear in the "Deleted" bin.
     * Auto-purge happens after 15 days via [purgeOldDeleted].
     */
    suspend fun softDelete(id: String) {
        dao.softDelete(id, LocalDateTime.now())
    }

    /**
     * Restores a soft-deleted transaction back to the active list.
     */
    suspend fun restore(id: String) = dao.restore(id)

    /**
     * Permanently removes a single transaction record regardless of its deleted state.
     */
    suspend fun hardDelete(id: String) = dao.hardDelete(id)

    /**
     * Permanently purges all soft-deleted transactions older than 15 days.
     * Should be called on app start or periodically.
     */
    suspend fun purgeOldDeleted() {
        val cutoff = LocalDate.now().minusDays(15).atStartOfDay()
        dao.clearOldDeleted(cutoff)
    }
}
