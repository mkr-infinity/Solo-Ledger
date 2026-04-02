package com.mkr.soloLedger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val recurringType: String,
    val nextDueDate: Long,
    val lastAddedDate: Long = 0L,
    val isActive: Boolean = true
)
