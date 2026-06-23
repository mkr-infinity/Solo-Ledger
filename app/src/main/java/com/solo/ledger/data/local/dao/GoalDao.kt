package com.solo.ledger.data.local.dao

import androidx.room.*
import com.solo.ledger.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<GoalEntity>>
    @Insert suspend fun insert(goal: GoalEntity): Long
    @Update suspend fun update(goal: GoalEntity)
    @Delete suspend fun delete(goal: GoalEntity)
    @Query("SELECT * FROM goals") suspend fun all(): List<GoalEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(items: List<GoalEntity>)
}
