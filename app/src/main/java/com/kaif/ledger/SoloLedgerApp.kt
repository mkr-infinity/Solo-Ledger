package com.kaif.ledger

import android.app.Application
import com.kaif.ledger.data.database.AppDatabase
import com.kaif.ledger.data.database.entity.AppSettingsEntity
import com.kaif.ledger.data.database.entity.CategoryEntity
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltAndroidApp
class SoloLedgerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database with default data
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(applicationContext)
            val categoryDao = database.categoryDao()
            val settingsDao = database.settingsDao()

            // Initialize categories if not already present
            if (categoryDao.getCategoryCount() == 0) {
                initializeDefaultCategories(categoryDao)
            }

            // Initialize settings if not already present
            val existingSettings = database.settingsDao().getSettings()
        }
    }

    private suspend fun initializeDefaultCategories(categoryDao: com.kaif.ledger.data.database.dao.CategoryDao) {
        val defaultCategories = listOf(
            CategoryEntity(name = "Dining", icon = "dining", colorHex = "#8B4513"),
            CategoryEntity(name = "Retail", icon = "retail", colorHex = "#FF6B9D"),
            CategoryEntity(name = "Transit", icon = "transit", colorHex = "#4A90E2"),
            CategoryEntity(name = "Wellness", icon = "wellness", colorHex = "#50C878"),
            CategoryEntity(name = "Gadgets", icon = "gadgets", colorHex = "#FFB347"),
            CategoryEntity(name = "Dividends", icon = "dividends", colorHex = "#FFD700"),
            CategoryEntity(name = "Entertainment", icon = "entertainment", colorHex = "#9B59B6"),
            CategoryEntity(name = "Utilities", icon = "utilities", colorHex = "#34495E"),
            CategoryEntity(name = "Other", icon = "other", colorHex = "#95A5A6"),
            CategoryEntity(name = "Apparel", icon = "apparel", colorHex = "#E74C3C")
        )

        for (category in defaultCategories) {
            categoryDao.insertCategory(category)
        }

        // Initialize default settings
        val defaultSettings = AppSettingsEntity(
            currentTheme = "MIDNIGHT",
            monthlyBudget = 5000.0,
            dailyBudgetLimit = 250.0,
            userName = "User",
            currencyCode = "USD"
        )
        val database = AppDatabase.getInstance(applicationContext)
        database.settingsDao().insertSettings(defaultSettings)
    }
}
