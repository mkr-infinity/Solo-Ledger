package com.kaif.ledger.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kaif.ledger.data.database.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert
    suspend fun insertSettings(settings: AppSettingsEntity): Long

    @Update
    suspend fun updateSettings(settings: AppSettingsEntity)

    @Query("SELECT * FROM app_settings LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("UPDATE app_settings SET currentTheme = :theme")
    suspend fun updateTheme(theme: String)

    @Query("UPDATE app_settings SET monthlyBudget = :budget")
    suspend fun updateMonthlyBudget(budget: Double)

    @Query("UPDATE app_settings SET showCategoryField = :show")
    suspend fun updateShowCategoryField(show: Boolean)

    @Query("UPDATE app_settings SET showNotesField = :show")
    suspend fun updateShowNotesField(show: Boolean)

    @Query("UPDATE app_settings SET showTitleField = :show")
    suspend fun updateShowTitleField(show: Boolean)

    @Query("UPDATE app_settings SET showDateField = :show")
    suspend fun updateShowDateField(show: Boolean)

    @Query("UPDATE app_settings SET userName = :name")
    suspend fun updateUserName(name: String)

    @Query("UPDATE app_settings SET currencyCode = :code")
    suspend fun updateCurrency(code: String)
}
