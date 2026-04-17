package com.kaif.ledger.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kaif.ledger.data.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllActiveExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 1 ORDER BY date DESC")
    fun getAllDeletedExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND date >= :startDate AND date <= :endDate AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategoryAndDateRange(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0 AND date >= :startDate AND date <= :endDate")
    fun getTotalExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double?>

    @Query("UPDATE expenses SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteExpense(id: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun permanentlyDeleteExpense(id: Long)

    @Query("DELETE FROM expenses WHERE isDeleted = 1")
    suspend fun clearDeletedExpenses()

    @Query("UPDATE expenses SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreExpense(id: Long)
}
