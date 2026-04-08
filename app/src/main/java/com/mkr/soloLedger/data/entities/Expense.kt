package com.mkr.soloLedger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isRecurring: Boolean = false,
    val recurringType: String = "NONE"
)
