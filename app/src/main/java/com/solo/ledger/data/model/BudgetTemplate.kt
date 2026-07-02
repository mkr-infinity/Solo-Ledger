package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_templates")
data class BudgetTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val monthlyBudget: Double,
    val description: String,
    val isDefault: Boolean = false
)
