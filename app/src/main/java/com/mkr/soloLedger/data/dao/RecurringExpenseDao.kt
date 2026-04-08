package com.mkr.soloLedger.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mkr.soloLedger.data.entities.RecurringExpense

@Dao
interface RecurringExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurringExpense: RecurringExpense): Long

    @Update
    suspend fun update(recurringExpense: RecurringExpense)

    @Delete
    suspend fun delete(recurringExpense: RecurringExpense)

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 ORDER BY nextDueDate ASC")
    fun getActiveRecurringExpenses(): LiveData<List<RecurringExpense>>

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 ORDER BY nextDueDate ASC")
    suspend fun getActiveRecurringExpensesOnce(): List<RecurringExpense>

    @Query("SELECT * FROM recurring_expenses WHERE nextDueDate <= :currentDate AND isActive = 1")
    suspend fun getDueRecurringExpenses(currentDate: Long): List<RecurringExpense>

    @Query("DELETE FROM recurring_expenses")
    suspend fun deleteAll()
}
