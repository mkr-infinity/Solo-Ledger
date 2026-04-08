package com.mkr.soloLedger.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mkr.soloLedger.data.entities.Budget

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Query("SELECT * FROM budget ORDER BY id DESC LIMIT 1")
    fun getBudget(): LiveData<Budget?>

    @Query("SELECT * FROM budget ORDER BY id DESC LIMIT 1")
    suspend fun getBudgetOnce(): Budget?

    @Query("DELETE FROM budget")
    suspend fun deleteAll()
}
