package com.solo.ledger.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solo.ledger.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear ORDER BY categoryId ASC")
    fun getByMonth(monthYear: String): Flow<List<Budget>>

    @Query("""
        SELECT * FROM budgets
        WHERE monthYear = :monthYear
          AND categoryId = :categoryId
        LIMIT 1
    """)
    suspend fun getByCategoryAndMonth(categoryId: String, monthYear: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("""
        SELECT * FROM budgets
        WHERE monthYear = :monthYear
          AND categoryId IS NULL
        LIMIT 1
    """)
    suspend fun getGlobalBudget(monthYear: String): Budget?

    @Query("DELETE FROM budgets WHERE monthYear = :monthYear AND categoryId = :categoryId")
    suspend fun deleteByCategoryAndMonth(categoryId: String, monthYear: String)

    @Query("DELETE FROM budgets WHERE monthYear = :monthYear AND categoryId IS NULL")
    suspend fun deleteGlobalBudget(monthYear: String)
}
