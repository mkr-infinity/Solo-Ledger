package com.solo.ledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val dateEpochDay: Long,      // LocalDate.toEpochDay()
    val timeMinutes: Int,        // minutes since midnight
    val notes: String = "",
    val attachmentUri: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
