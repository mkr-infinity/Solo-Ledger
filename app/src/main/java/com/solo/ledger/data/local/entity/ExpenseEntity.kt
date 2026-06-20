package com.solo.ledger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("categoryId"),
        Index("dateEpochDay"),
        Index("deletedAtMillis"),
    ],
)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amountMinor: Long,
    val currencyCode: String,
    val categoryId: String,
    val dateEpochDay: Long,
    val timeMinuteOfDay: Int,
    val notes: String?,
    val attachmentPath: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val deletedAtMillis: Long?,
)
