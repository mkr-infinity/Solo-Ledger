package com.kaif.ledger.data.model

import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val category: Category? = null,
    val date: LocalDateTime,
    val notes: String = "",
    val isDeleted: Boolean = false
)

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String
)

data class AppSettings(
    val id: Long = 0,
    val currentTheme: String = "MIDNIGHT",
    val showCategoryField: Boolean = true,
    val showNotesField: Boolean = true,
    val showTitleField: Boolean = true,
    val showDateField: Boolean = true,
    val currencyCode: String = "USD",
    val monthlyBudget: Double = 5000.0,
    val dailyBudgetLimit: Double = 250.0,
    val userName: String = "User"
)
