package com.mkr.soloLedger.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mkr.soloLedger.data.entities.Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpensesOnce(): List<Expense>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesByDateRangeOnce(startDate: Long, endDate: Long): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalAmountByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE title LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchExpenses(query: String): LiveData<List<Expense>>

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    suspend fun getCategoryTotals(startDate: Long, endDate: Long): List<CategoryTotal>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

data class CategoryTotal(val categoryId: Long, val total: Double)
