package com.solo.ledger

import android.app.Application
import com.solo.ledger.data.database.SoloLedgerDatabase
import com.solo.ledger.data.repository.ExpenseRepository
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.SavingsGoalRepository
import com.solo.ledger.data.preferences.UserPreferences

class SoloLedgerApp : Application() {

    val database by lazy { SoloLedgerDatabase.getDatabase(this) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val savingsGoalRepository by lazy { SavingsGoalRepository(database.savingsGoalDao()) }
    val userPreferences by lazy { UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SoloLedgerApp
            private set
    }
}
