package com.solo.ledger.core

import android.content.Context
import com.solo.ledger.data.datastore.SettingsRepository
import com.solo.ledger.data.local.LedgerDatabase
import com.solo.ledger.data.repository.BackupManager
import com.solo.ledger.data.repository.LedgerRepository

/** Minimal manual dependency container so the app builds without DI codegen setup. */
object ServiceLocator {
    lateinit var ledgerRepository: LedgerRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var backupManager: BackupManager
        private set

    fun init(context: Context) {
        val db = LedgerDatabase.get(context)
        ledgerRepository = LedgerRepository(db.expenseDao(), db.categoryDao(), db.goalDao())
        settingsRepository = SettingsRepository(context.applicationContext)
        backupManager = BackupManager(ledgerRepository)
    }
}
