package com.solo.ledger.data.repository

import com.solo.ledger.data.local.dao.GoalDao
import com.solo.ledger.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
) {
    fun observeActiveGoals(): Flow<List<SavingsGoalEntity>> = goalDao.observeActiveGoals()

    fun observeGoal(id: String): Flow<SavingsGoalEntity?> = goalDao.observeGoal(id)

    suspend fun upsert(goal: SavingsGoalEntity) = goalDao.upsert(goal)

    suspend fun archive(id: String, archivedAtMillis: Long) = goalDao.archive(id, archivedAtMillis)
}
