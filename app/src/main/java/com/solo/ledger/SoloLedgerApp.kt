package com.solo.ledger

import android.app.Application
import com.solo.ledger.data.database.SoloLedgerDatabase
import com.solo.ledger.data.model.LogType
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.ExpenseRepository
import com.solo.ledger.data.repository.LogRepository
import com.solo.ledger.data.repository.SavingsGoalRepository
import com.solo.ledger.data.preferences.UserPreferences

class SoloLedgerApp : Application() {

    val database by lazy { SoloLedgerDatabase.getDatabase(this) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val savingsGoalRepository by lazy { SavingsGoalRepository(database.savingsGoalDao()) }
    val userPreferences by lazy { UserPreferences(this) }
    val logRepository by lazy { LogRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                logRepository.addLog(
                    type = LogType.CRASH,
                    title = "CRASH: ${throwable::class.simpleName}: ${throwable.message?.take(100) ?: "Unknown"}",
                    details = "Thread: ${thread.name} | Stack: ${throwable.stackTraceToString().take(500)}"
                )
            } catch (_: Exception) {
                // Don't crash while logging a crash
            }
            // Call the default handler to show system crash dialog
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        lateinit var instance: SoloLedgerApp
            private set
    }
}
