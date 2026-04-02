package com.mkr.soloLedger.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mkr.soloLedger.data.database.SoloLedgerDatabase
import com.mkr.soloLedger.data.entities.Expense
import com.mkr.soloLedger.data.entities.UserProfile
import com.mkr.soloLedger.data.repository.SoloLedgerRepository
import com.mkr.soloLedger.utils.getEndOfMonth
import com.mkr.soloLedger.utils.getStartOfMonth
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SoloLedgerRepository
    val userProfile: LiveData<UserProfile?>
    val recentExpenses: LiveData<List<Expense>>
    val currentMonthExpenses: LiveData<List<Expense>>

    init {
        val db = SoloLedgerDatabase.getDatabase(application)
        repository = SoloLedgerRepository(
            db.expenseDao(), db.categoryDao(), db.budgetDao(),
            db.userProfileDao(), db.recurringExpenseDao()
        )
        userProfile = repository.userProfile
        recentExpenses = repository.getRecentExpenses(5)
        currentMonthExpenses = repository.getExpensesByDateRange(getStartOfMonth(), getEndOfMonth())
    }

    fun updateUserProfile(profile: UserProfile) = viewModelScope.launch {
        repository.updateUserProfile(profile)
    }

    fun generateInsight(totalSpent: Double, budget: Double): String {
        if (budget <= 0) return "Set a monthly budget to track your spending 💡"
        val percentage = (totalSpent / budget) * 100
        return when {
            percentage >= 100 -> "🚨 Budget exceeded! You've spent ${"%.0f".format(percentage)}% of your budget."
            percentage >= 80 -> "⚠️ Warning: You've used ${"%.0f".format(percentage)}% of your budget. Slow down!"
            percentage >= 50 -> "📊 You've spent ${"%.0f".format(percentage)}% of your budget this month."
            else -> "✅ You're within budget! ${"%.0f".format(percentage)}% used. Great job! 🎉"
        }
    }
}
