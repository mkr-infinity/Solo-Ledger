package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val title: String?,
    val notes: String?,
    val tags: String?, // comma-separated
    val date: LocalDate,
    val time: LocalTime,
    val dayOfWeek: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean = false,
    val deletedAt: LocalDateTime? = null
)

enum class TransactionType { INCOME, EXPENSE }
