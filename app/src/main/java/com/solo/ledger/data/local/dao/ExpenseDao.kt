package com.solo.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE deletedAtMillis IS NULL ORDER BY dateEpochDay DESC, timeMinuteOfDay DESC")
    fun observeActiveExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE deletedAtMillis IS NOT NULL ORDER BY deletedAtMillis DESC")
    fun observeDeletedExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun observeExpense(id: String): Flow<ExpenseEntity?>

    @Query(
        "SELECT * FROM expenses " +
            "WHERE deletedAtMillis IS NULL " +
            "AND (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') " +
            "ORDER BY dateEpochDay DESC, timeMinuteOfDay DESC",
    )
    fun searchActiveExpenses(query: String): Flow<List<ExpenseEntity>>

    @Query(
        "SELECT * FROM expenses " +
            "WHERE deletedAtMillis IS NULL " +
            "AND dateEpochDay BETWEEN :startEpochDay AND :endEpochDay " +
            "ORDER BY dateEpochDay DESC, timeMinuteOfDay DESC",
    )
    fun observeExpensesBetween(startEpochDay: Long, endEpochDay: Long): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("UPDATE expenses SET deletedAtMillis = :deletedAtMillis, updatedAtMillis = :deletedAtMillis WHERE id = :id")
    suspend fun moveToBin(id: String, deletedAtMillis: Long)

    @Query("UPDATE expenses SET deletedAtMillis = NULL, updatedAtMillis = :restoredAtMillis WHERE id = :id")
    suspend fun restore(id: String, restoredAtMillis: Long)

    @Delete
    suspend fun deletePermanently(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE deletedAtMillis IS NOT NULL")
    suspend fun clearBin()
}
