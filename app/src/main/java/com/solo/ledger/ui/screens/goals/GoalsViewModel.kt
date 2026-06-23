package com.solo.ledger.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GoalsViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    val goals: StateFlow<List<GoalEntity>> =
        repo.goals().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(title: String, target: Double) = viewModelScope.launch {
        repo.addGoal(GoalEntity(title = title, targetAmount = target))
    }
    fun contribute(goal: GoalEntity, amount: Double) = viewModelScope.launch {
        repo.updateGoal(goal.copy(savedAmount = (goal.savedAmount + amount).coerceAtMost(goal.targetAmount)))
    }
    fun delete(goal: GoalEntity) = viewModelScope.launch { repo.deleteGoal(goal) }
}
