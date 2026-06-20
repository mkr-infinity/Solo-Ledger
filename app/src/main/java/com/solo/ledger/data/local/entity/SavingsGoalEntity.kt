package com.solo.ledger.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings_goals",
    indices = [Index("targetDateEpochDay")],
)
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetAmountMinor: Long,
    val savedAmountMinor: Long,
    val currencyCode: String,
    val targetDateEpochDay: Long?,
    val accentColorHex: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val archivedAtMillis: Long?,
)
