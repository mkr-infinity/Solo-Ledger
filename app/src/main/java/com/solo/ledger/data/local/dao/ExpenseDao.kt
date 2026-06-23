package com.solo.ledger.data.local.dao

import androidx.room.*
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.domain.model.CategorySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY dateEpochDay DESC, timeMinutes DESC")
    fun observeActive(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND dateEpochDay BETWEEN :start AND :end ORDER BY dateEpochDay DESC, timeMinutes DESC")
    fun observeRange(start: Long, end: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT category, SUM(amount) AS total, COUNT(*) AS count FROM expenses WHERE isDeleted = 0 AND dateEpochDay BETWEEN :start AND :end GROUP BY category ORDER BY total DESC")
    fun observeCategorySummary(start: Long, end: Long): Flow<List<CategorySummary>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE isDeleted = 0 AND dateEpochDay BETWEEN :start AND :end")
    fun observeTotalInRange(start: Long, end: Long): Flow<Double>

    @Query("SELECT * FROM expenses WHERE id = :id") suspend fun getById(id: Long): ExpenseEntity?
    @Query("SELECT * FROM expenses") suspend fun all(): List<ExpenseEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(items: List<ExpenseEntity>)

    @Insert suspend fun insert(expense: ExpenseEntity): Long
    @Update suspend fun update(expense: ExpenseEntity)
    @Query("UPDATE expenses SET isDeleted = 1, deletedAt = :ts WHERE id = :id") suspend fun softDelete(id: Long, ts: Long)
    @Query("UPDATE expenses SET isDeleted = 0, deletedAt = NULL WHERE id = :id") suspend fun restore(id: Long)
    @Query("DELETE FROM expenses WHERE id = :id") suspend fun hardDelete(id: Long)
    @Query("DELETE FROM expenses WHERE isDeleted = 1") suspend fun clearBin()
}
