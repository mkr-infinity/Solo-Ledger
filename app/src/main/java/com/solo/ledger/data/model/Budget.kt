package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String,
    val categoryId: String?,           // null = global monthly budget
    val monthYear: String,             // Format: "YYYY-MM"
    val limitAmount: Double,
    val createdAt: LocalDateTime
)
