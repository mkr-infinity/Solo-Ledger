package com.solo.ledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val iconKey: String,
    val colorArgb: Long,
    val isDefault: Boolean = false
)
