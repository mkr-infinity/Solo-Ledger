package com.solo.ledger.data

import android.content.Context
import com.solo.ledger.data.local.SoloLedgerDatabase
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.ExpenseRepository
import com.solo.ledger.data.repository.GoalRepository
import com.solo.ledger.data.repository.SettingsRepository

class AppContainer(context: Context) {
    private val database = SoloLedgerDatabase.create(context)

    val ledgerDatabase = database
    val expenseRepository = ExpenseRepository(database.expenseDao())
    val categoryRepository = CategoryRepository(database.categoryDao())
    val goalRepository = GoalRepository(database.goalDao())
    val settingsRepository = SettingsRepository(context)
}
