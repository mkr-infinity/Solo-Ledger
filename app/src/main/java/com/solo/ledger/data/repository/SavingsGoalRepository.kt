package com.solo.ledger.data.repository

import com.solo.ledger.data.dao.SavingsGoalDao
import com.solo.ledger.data.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

class SavingsGoalRepository(private val savingsGoalDao: SavingsGoalDao) {

    fun getAllGoals(): Flow<List<SavingsGoal>> = savingsGoalDao.getAllGoals()

    suspend fun getGoalById(id: Long): SavingsGoal? = savingsGoalDao.getGoalById(id)

    suspend fun insertGoal(goal: SavingsGoal): Long = savingsGoalDao.insertGoal(goal)

    suspend fun updateGoal(goal: SavingsGoal) = savingsGoalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: SavingsGoal) = savingsGoalDao.deleteGoal(goal)
}
