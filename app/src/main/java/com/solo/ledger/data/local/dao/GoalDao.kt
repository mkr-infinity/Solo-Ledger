package com.solo.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solo.ledger.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM savings_goals WHERE archivedAtMillis IS NULL ORDER BY createdAtMillis DESC")
    fun observeActiveGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
    fun observeGoal(id: String): Flow<SavingsGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: SavingsGoalEntity)

    @Update
    suspend fun update(goal: SavingsGoalEntity)

    @Query("UPDATE savings_goals SET archivedAtMillis = :archivedAtMillis, updatedAtMillis = :archivedAtMillis WHERE id = :id")
    suspend fun archive(id: String, archivedAtMillis: Long)
}
