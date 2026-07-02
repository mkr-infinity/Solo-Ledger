package com.solo.ledger.data.dao

import androidx.room.*
import com.solo.ledger.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC, time DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') ORDER BY date DESC")
    fun searchExpenses(query: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0 AND date BETWEEN :startDate AND :endDate")
    fun getTotalSpendingByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0 AND date = :date")
    fun getDailySpending(date: Long): Flow<Double?>

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE isDeleted = 0 AND date BETWEEN :startDate AND :endDate GROUP BY categoryId ORDER BY total DESC")
    fun getCategorySpending(startDate: Long, endDate: Long): Flow<List<CategorySpending>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("UPDATE expenses SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteExpense(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE expenses SET isDeleted = 0, deletedAt = null WHERE id = :id")
    suspend fun restoreExpense(id: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun permanentlyDeleteExpense(id: Long)

    @Query("DELETE FROM expenses WHERE isDeleted = 1")
    suspend fun clearBin()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<Expense>)
}

data class CategorySpending(
    val categoryId: Long,
    val total: Double
)
