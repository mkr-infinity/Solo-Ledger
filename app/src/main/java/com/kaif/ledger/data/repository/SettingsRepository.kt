package com.kaif.ledger.data.repository

import com.kaif.ledger.data.database.dao.SettingsDao
import com.kaif.ledger.data.database.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    fun getSettings(): Flow<AppSettingsEntity?> {
        return settingsDao.getSettings()
    }

    suspend fun updateSettings(settings: AppSettingsEntity) {
        settingsDao.updateSettings(settings)
    }

    suspend fun updateTheme(theme: String) {
        settingsDao.updateTheme(theme)
    }

    suspend fun updateMonthlyBudget(budget: Double) {
        settingsDao.updateMonthlyBudget(budget)
    }

    suspend fun updateShowCategoryField(show: Boolean) {
        settingsDao.updateShowCategoryField(show)
    }

    suspend fun updateShowNotesField(show: Boolean) {
        settingsDao.updateShowNotesField(show)
    }

    suspend fun updateShowTitleField(show: Boolean) {
        settingsDao.updateShowTitleField(show)
    }

    suspend fun updateShowDateField(show: Boolean) {
        settingsDao.updateShowDateField(show)
    }

    suspend fun updateUserName(name: String) {
        settingsDao.updateUserName(name)
    }

    suspend fun updateCurrency(code: String) {
        settingsDao.updateCurrency(code)
    }
}
