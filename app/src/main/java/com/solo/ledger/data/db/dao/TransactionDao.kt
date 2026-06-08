package com.solo.ledger.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solo.ledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY date DESC, time DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getAllDeleted(): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions
        WHERE isDeleted = 0
          AND date >= :start
          AND date <= :end
        ORDER BY date DESC, time DESC
    """)
    fun getByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions
        WHERE isDeleted = 0
          AND categoryId = :categoryId
        ORDER BY date DESC, time DESC
    """)
    fun getByCategory(categoryId: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions
        WHERE isDeleted = 0
          AND date LIKE :monthYearPrefix || '%'
        ORDER BY date DESC, time DESC
    """)
    fun getByMonth(monthYearPrefix: String): Flow<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions
        WHERE isDeleted = 0
          AND type = :type
          AND date LIKE :monthYearPrefix || '%'
    """)
    suspend fun getSumByTypeAndMonth(type: String, monthYearPrefix: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Query("UPDATE transactions SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: LocalDateTime)

    @Query("UPDATE transactions SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: String)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("DELETE FROM transactions WHERE isDeleted = 1 AND deletedAt < :cutoffDate")
    suspend fun clearOldDeleted(cutoffDate: LocalDateTime)

    @Query("""
        SELECT * FROM transactions
        WHERE isDeleted = 0
        ORDER BY date DESC, time DESC
        LIMIT :limit
    """)
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Transaction?
}
