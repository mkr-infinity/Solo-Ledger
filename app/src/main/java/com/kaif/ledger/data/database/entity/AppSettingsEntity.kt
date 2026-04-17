package com.kaif.ledger.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val currentTheme: String = "MIDNIGHT",
    val showCategoryField: Boolean = true,
    val showNotesField: Boolean = true,
    val showTitleField: Boolean = true,
    val showDateField: Boolean = true,
    val currencyCode: String = "USD",
    val monthlyBudget: Double = 5000.0,
    val dailyBudgetLimit: Double = 250.0,
    val userName: String = "User",
    val userProfileUrl: String = "",
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)
