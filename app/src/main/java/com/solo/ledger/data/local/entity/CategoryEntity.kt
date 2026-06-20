package com.solo.ledger.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val isArchived: Boolean,
)
